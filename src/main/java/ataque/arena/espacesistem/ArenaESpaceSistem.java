/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package ataque.arena.espacesistem;

/**
 *
 * @author RyanS
 */
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;



/**
 *
 * @author RyanS
 */
public class ArenaESpaceSistem {

    public static void main(String[] args) {
        // Inicialização de criptografia e carregamento de utilizadores
        Criptografia criptografia = new Criptografia();
        List<Utilizador> utilizadores = Utilizador.carregarUtilizadores(criptografia);

        // Inicializar admin padrão, se necessário
        Admin.inicializarAdminPadrao(criptografia);
        
        // Executar o menu principal
        Scanner scanner = new Scanner(System.in);
        Utilizador utilizadorLogado = null;

        while (true) {
            System.out.println("""
                    === Bem Vindo ao Arena-eSpace System ===
                    1 - Cadastrar-se
                    2 - Login
                    0 - Sair
                    Escolha uma opção:""");

            int opcao = scanner.nextInt();
            scanner.nextLine(); // Consumir a nova linha

            switch (opcao) {
                case 1 -> {
                    // Cadastro de utilizador
                    Utilizador.cadastroDeUtilizador(utilizadores, criptografia);
                }
                case 2 -> {
                    // Autenticação de utilizador
                    System.out.print("Digite o nome de utilizador: ");
                    String nomeDeUtilizador = scanner.nextLine();
                    System.out.print("Digite a senha: ");
                    String password = scanner.nextLine();

                    // Autenticar o utilizador
                    utilizadorLogado = Utilizador.autenticarUtilizador(nomeDeUtilizador, password, criptografia);

                    if (utilizadorLogado != null) {
                        String privilegio = utilizadorLogado.getPrivilegio();

                        switch (privilegio.toLowerCase()) {
                            case "admin" -> {
                                System.out.println("Bem-vindo, Administrador!");
                                Admin admin;
                                if (utilizadorLogado instanceof Admin) {
                                    admin = (Admin) utilizadorLogado;
                                } else {
                                    // Cria um admin baseado nos dados carregados
                                    admin = new Admin(
                                        utilizadorLogado.getNomeDeUtilizador(),
                                        utilizadorLogado.getEmail(),
                                        password, // Admin deve autenticar com sua senha original
                                        criptografia
                                    );
                                }
                                admin.exibirMenuAdmin(utilizadores, criptografia);
                            }
                            case "promotor" -> {
                            System.out.println("Bem-vindo, Promotor " + utilizadorLogado.getNomeCompleto() + "!");

                            Promotor promotor; // Declara fora do `if` para reutilizar a variável
                            if (utilizadorLogado instanceof Promotor) {
                                promotor = (Promotor) utilizadorLogado; // Faz o casting
                            } else {
                                promotor = new Promotor(
                                    utilizadorLogado.getNomeCompleto(),
                                    utilizadorLogado.getNomeDeUtilizador(),
                                    utilizadorLogado.getEmail(),
                                    utilizadorLogado.getPassword(),
                                    utilizadorLogado.getPrivilegio(),
                                    criptografia
                                );
                            }

                            // Chama o método de gerenciamento de eventos
                            gerirEvento(scanner, promotor);
                        }

                            default -> {
                                System.out.println("Bem-vindo, " + privilegio + " " + utilizadorLogado.getNomeCompleto() + "!");
                                // Aqui você pode adicionar menus específicos para outros tipos de utilizadores
                            }
                        }
                    } else {
                        System.out.println("Nome de utilizador ou senha inválidos!");
                    }
                }




                case 0 -> {
                    // Sair do sistema
                    System.out.println("Saindo do sistema...");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Opção inválida! Tente novamente.");
            }
        }
    }




    public static void gerirEvento(Scanner scanner, Promotor promotor) {
        while (true) {
            System.out.println("""
                    === Menu Gerir Eventos ===
                    1 - Criar Evento
                    2 - Editar Evento
                    3 - Listar Meus Eventos
                    4 - Excluir Evento
                    0 - Sair
                    Escolha uma opção:""");

            int opcao = scanner.nextInt();
            scanner.nextLine(); // Consumir quebra de linha

            switch (opcao) {
                case 1 -> Evento.criarEvento(scanner, promotor); // Criar novo evento
                case 2 -> Evento.editarEvento(scanner, promotor); // Editar evento existente
                case 3 -> Evento.listarEventosDePromotor(promotor); // Listar eventos do promotor
                case 4 -> Evento.excluirEvento(scanner, promotor); // Excluir evento
                case 0 -> {
                    System.out.println("Saindo do menu de gestão de eventos...");
                    return;
                }
                default -> System.out.println("Opção inválida! Tente novamente.");
            }
        }
    }
    

    public static OpcaoMenu1 mostrarMenuEDevolverOpcaoSelecionada(Scanner scanner) {
        OpcaoMenu1 opcaoSelecionada = null;
        boolean entradaValida = false;

        while (!entradaValida) {
            System.out.println("\n=== Arena-eSpace Menu Principal ===");
            for (OpcaoMenu1 opcao : OpcaoMenu1.values()) {
                System.out.println(opcao.codigoMenu1 + " - " + opcao.getDescricao());
            }
            System.out.print("Escolha uma opção: ");
            try {
                int codigo = scanner.nextInt();
                scanner.nextLine(); // Limpar buffer
                opcaoSelecionada = OpcaoMenu1.getFromCodigo(codigo);

                if (opcaoSelecionada != null) {
                    entradaValida = true;
                } else {
                    System.out.println("Opção inválida. Tente novamente.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Por favor, insira um número válido.");
                scanner.nextLine(); // Limpar buffer
            }
        }

        return opcaoSelecionada;
    }

    public enum OpcaoMenu1 {
        LOGIN(1, "Login"),
        REGISTO(2, "Registar Utilizador"),
        SAIR(0, "Sair");

        private final int codigoMenu1;
        private final String descricao;

        OpcaoMenu1(int codigoMenu1, String descricao) {
            this.codigoMenu1 = codigoMenu1;
            this.descricao = descricao;
        }

        public static OpcaoMenu1 getFromCodigo(int codigo) {
            for (OpcaoMenu1 opcao : OpcaoMenu1.values()) {
                if (opcao.codigoMenu1 == codigo) {
                    return opcao;
                }
            }
            return null;
        }

        public String getDescricao() {
            return descricao;
        }
    }

}


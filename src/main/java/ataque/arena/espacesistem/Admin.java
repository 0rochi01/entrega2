/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ataque.arena.espacesistem;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author RyanS
 */

public class Admin extends Utilizador {
    private static final long serialVersionUID = 1L; // Valor fixo
    
    // Construtor da classe Admin
    public Admin(String nomeDeUtilizador, String email, String password, Criptografia criptografia) {
        super("Administrador", nomeDeUtilizador, email, password, "Admin", criptografia);
    }  
    
    // Método para exibir o menu de administração
    public void exibirMenuAdmin(List<Utilizador> utilizadores, Criptografia criptografia) {
        Scanner scanner = new Scanner(System.in);
        OpcaoMenuAdmin opcao = null;

        do {
            System.out.println("\n=== Menu de Administração ===");
            for (OpcaoMenuAdmin op : OpcaoMenuAdmin.values()) {
                System.out.println(op.getCodigo() + " - " + op.name());
            }
            System.out.print("Escolha uma opção: ");
            int codigo;
            try {
                codigo = scanner.nextInt();
                scanner.nextLine(); // Consumir quebra de linha
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Por favor, insira um número.");
                scanner.nextLine(); // Limpar entrada inválida
                continue;
            }

            opcao = OpcaoMenuAdmin.getFromCodigo(codigo);

            if (opcao != null) {
                switch (opcao) {
                    case GERENCIAR_UTILIZADORES -> {
                        gerenciarUtilizadores(criptografia, utilizadores);
                    }
                    case SAIR -> System.out.println("Retornando ao menu principal...");
                    default -> System.out.println("Opção inválida, tente novamente.");
                }
            } else {
                System.out.println("Opção inválida, tente novamente.");
            }
        } while (opcao != OpcaoMenuAdmin.SAIR);
    }


    // Classe Enum interna para representar as opções do menu
    public enum OpcaoMenuAdmin {
        GERENCIAR_UTILIZADORES(1),
        SAIR(0);

        private final int codigo;

        OpcaoMenuAdmin(int codigo) {
            this.codigo = codigo;
        }

        public int getCodigo() {
            return codigo;
        }

        public static OpcaoMenuAdmin getFromCodigo(int codigo) {
            for (OpcaoMenuAdmin opcao : OpcaoMenuAdmin.values()) {
                if (opcao.getCodigo() == codigo) {
                    return opcao;
                }
            }
            return null;
        }
    }
    
    
    // Inicializar o administrador padrão
    public static void inicializarAdminPadrao(Criptografia criptografia) {
        List<Utilizador> utilizadores = Utilizador.carregarUtilizadores(criptografia);

        boolean adminExiste = utilizadores.stream().anyMatch(u -> u.getPrivilegio().equalsIgnoreCase("Admin"));
        
        // Cria o administrador padrão
        if (!adminExiste) {
            Admin admin = new Admin("admin", "admin@arenaespace.pt", "adminArena", criptografia);
            Utilizador.salvarUtilizador(admin, criptografia); // Salva admin no mesmo arquivo
            System.out.println("Administrador padrão criado.");
        } else {
            System.out.println("Administrador já existe.");
        }
    }
    
    
    public static void gerenciarUtilizadores(Criptografia criptografia, List<Utilizador> utilizadores) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n=== Gerenciar Utilizadores ===");
        System.out.print("Digite o privilégio (Promotor, Lider de Equipa, Jogador, Espectador): ");
        String privilegio = scanner.nextLine();

        // Filtrar utilizadores com o privilégio especificado
        List<Utilizador> utilizadoresFiltrados = utilizadores.stream()
            .filter(u -> u.getPrivilegio().equalsIgnoreCase(privilegio))
            .toList();

        if (utilizadoresFiltrados.isEmpty()) {
            System.out.println("Nenhum utilizador encontrado com o privilégio: " + privilegio);
            return;
        }

        // Listar os utilizadores encontrados
        System.out.println("\nUtilizadores com privilégio " + privilegio + ":");
        for (Utilizador u : utilizadoresFiltrados) {
            System.out.println("- Nome de Utilizador: " + u.getNomeDeUtilizador() + ", Nome Completo: " + u.getNomeCompleto());
        }

        System.out.print("\nDigite o nome de utilizador que deseja gerenciar: ");
        String nomeDeUtilizador = scanner.nextLine();

        // Encontrar o utilizador correspondente
        Utilizador utilizadorAlvo = utilizadoresFiltrados.stream()
            .filter(u -> u.getNomeDeUtilizador().equalsIgnoreCase(nomeDeUtilizador))
            .findFirst()
            .orElse(null);

        if (utilizadorAlvo == null) {
            System.out.println("Utilizador com o nome '" + nomeDeUtilizador + "' não encontrado.");
            return;
        }

        // Menu de ações: Editar, Excluir ou Listar
        System.out.println("\nO que deseja fazer com o utilizador " + nomeDeUtilizador + "?");
        System.out.println("1 - Editar");
        System.out.println("2 - Excluir");
        System.out.println("3 - Listar Informações");
        System.out.print("Escolha uma opção: ");
        int opcao = scanner.nextInt();
        scanner.nextLine(); // Consumir a nova linha

        switch (opcao) {
            case 1 -> {
                // Editar o utilizador

                // Editar o email
                System.out.print("Digite o novo Email (ou pressione Enter para manter o atual): ");
                String novoEmail = scanner.nextLine();
                if (!novoEmail.isEmpty()) {
                    utilizadorAlvo.setEmail(novoEmail);
                }

                // Editar a senha
                System.out.print("Digite a nova Senha (ou pressione Enter para manter a atual): ");
                String novaSenha = scanner.nextLine();
                if (!novaSenha.isEmpty()) {
                    utilizadorAlvo.setPassword(novaSenha);
                }

                // Salvar alterações
                Utilizador.salvarTodoUtilizador(utilizadores, criptografia);
                System.out.println("Utilizador atualizado com sucesso.");
            }
            case 2 -> {
                // Excluir o utilizador com validação pelo enum simNao
                System.out.println("Tem certeza que deseja excluir o utilizador " + utilizadorAlvo.getNomeDeUtilizador() + "?");
                System.out.print("Digite '1' para SIM ou '0' para NÃO: ");

                int validacao;
                try {
                    validacao = scanner.nextInt();
                    scanner.nextLine(); // Consumir a nova linha
                } catch (InputMismatchException e) {
                    System.out.println("Entrada inválida. Exclusão cancelada.");
                    scanner.nextLine(); // Limpar entrada inválida
                    return;
                }

                // Validar a entrada usando o enum simNao
                simNao confirmacao = simNao.getFromCodigo(validacao);

                if (confirmacao != null) {
                    switch (confirmacao) {
                        case SIM -> {
                            utilizadores.remove(utilizadorAlvo);
                            Utilizador.salvarTodoUtilizador(utilizadores, criptografia); // Salvar lista atualizada
                            System.out.println("Utilizador excluído com sucesso.");
                        }
                        case NAO -> System.out.println("Ação de exclusão cancelada.");
                    }
                } else {
                    System.out.println("Opção inválida. Exclusão cancelada.");
                }
            }
            case 3 -> {
                // Listar informações do utilizador
                System.out.println("\nInformações do Utilizador:");
                System.out.println("Nome Completo: " + utilizadorAlvo.getNomeCompleto());
                System.out.println("Nome de Utilizador: " + utilizadorAlvo.getNomeDeUtilizador());
                System.out.println("Email: " + utilizadorAlvo.getEmail());
                System.out.println("Privilégio: " + utilizadorAlvo.getPrivilegio());
            }
            default -> System.out.println("Opção inválida. Retornando ao menu principal.");
        }
    }
    
    // Classe Enum interna para representar as opções do menu
    public enum simNao {
        SIM(1),
        NAO(0);

        private final int validacao;

        simNao(int validacao) {
            this.validacao = validacao;
        }

        public int getValidacao() {
            return validacao;
        }

        public static simNao getFromCodigo(int validacao) {
            for (simNao op : simNao.values()) {
                if (op.getValidacao() == validacao) {
                    return op;
                }
            }
            return null;
        }
    }
}

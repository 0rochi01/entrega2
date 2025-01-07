/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ataque.arena.espacesistem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author RyanS
 */

public class Utilizador implements Serializable {
    private static final long serialVersionUID = 1L; // Adicione um ID de versão
    
    // Atributos da classe Utilizador
    private String nomeCompleto; // Nome completo do utilizador
    private final String nomeDeUtilizador; // Nome de utilizador (username)
    private String email; // Email do utilizador (armazenado criptografado)
    private String password; // Senha do utilizador (armazenada como hash)
    private String privilegio; // Privilégio do utilizador (armazenado criptografado)
    
    private static Set<String> cacheNomesDeUtilizador = new HashSet<>();
    private static List<Utilizador> utilizadoresCadastrados = new ArrayList<>();
    
    static final String ARQUIVO_UTILIZADORES = "utilizadores_dados.txt";
    protected transient Criptografia criptografia;

    public Utilizador(String nomeCompleto, String nomeDeUtilizador, String email, String password, String privilegio, Criptografia criptografia) {
        this.nomeCompleto = nomeCompleto;
        this.nomeDeUtilizador = nomeDeUtilizador;
        this.email = criptografia.criptografar(email);
        this.password = criptografia.gerarHashDeSenha(password);
        this.privilegio = criptografia.criptografar(privilegio);
        this.criptografia = criptografia;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getNomeDeUtilizador() {
        return nomeDeUtilizador;
    }

    public String getEmail() {
        try {
            return criptografia.descriptografar(email);
        } catch (RuntimeException e) {
            System.out.println("Erro ao descriptografar o email: " + e.getMessage());
            return null; // Retorna null ou uma string padrão em caso de erro
        }
    }

    public void setEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email não pode ser vazio ou nulo.");
        }
        this.email = criptografia.criptografar(email);
    }


    public String getPassword() {
        return password; // Retorna o hash da senha, não a senha em texto claro
    }

    public void setPassword(String password) {
        this.password = criptografia.gerarHashDeSenha(password);
    }

    public String getPrivilegio() {
        try {
            return criptografia.descriptografar(privilegio);
        } catch (RuntimeException e) {
            System.out.println("Erro ao descriptografar o privilégio: " + e.getMessage());
            return null;
        }
    }

    public void setPrivilegio(String privilegio) {
        try {
            this.privilegio = criptografia.criptografar(privilegio);
        } catch (RuntimeException e) {
            System.out.println("Erro ao criptografar o privilégio: " + e.getMessage());
        }
    }
    
    
    static List<Utilizador> carregarUtilizadores(Criptografia criptografia) {
        List<Utilizador> utilizadores = new ArrayList<>();
        File arquivo = new File(ARQUIVO_UTILIZADORES);

        // Retorna lista vazia se o arquivo não existir
        if (!arquivo.exists()) {
            return utilizadores;
        }

        if (criptografia == null){
            throw new IllegalStateException("Instância de Criptografia não foi inicializada.");
        }

        // Tenta ler e desserializar os utilizadores do arquivo
        try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO_UTILIZADORES))) {
            String linha;

            while ((linha = reader.readLine()) != null) {
                try {
                    // Descriptografar a linha usando a instância de Criptografia
                    String dadosDecodificados = criptografia.descriptografar(linha);

                    // Decodificar a string Base64 e desserializar o objeto
                    byte[] dadosDeserializados = Base64.getDecoder().decode(dadosDecodificados);
                    try (ByteArrayInputStream bais = new ByteArrayInputStream(dadosDeserializados);
                         ObjectInputStream ois = new ObjectInputStream(bais)) {

                        // Adiciona o utilizador à lista
                        Utilizador utilizador = (Utilizador) ois.readObject();
                        utilizadores.add(utilizador);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Erro ao desserializar um utilizador: " + e.getMessage());
                    e.printStackTrace(); // Adiciona o rastreamento do erro para depuração
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao carregar utilizadores do arquivo: " + e.getMessage());
            e.printStackTrace(); // Adiciona o rastreamento do erro para depuração
        }

        return utilizadores;
    }
    
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject(); // Realiza a desserialização padrão
        this.criptografia = new Criptografia(); // Reinstancia o campo criptografia
    }


    public static Utilizador autenticarUtilizador(String nomeDeUtilizador, String password, Criptografia criptografia) {
        // Carrega os utilizadores usando o método estático carregarUtilizadores
        List<Utilizador> utilizadores = carregarUtilizadores(criptografia);
        // Percorre a lista de utilizadores
        for (Utilizador utilizador : utilizadores) {
            // Verifica se o nome de utilizador coincide
            if (utilizador.getNomeDeUtilizador().equalsIgnoreCase(nomeDeUtilizador)) {
                // Verifica a senha usando o método de comparação segura
                if (criptografia.verificarSenha(password, utilizador.getPassword())) {
                    System.out.println("Autenticação bem-sucedida!");
                    return utilizador; // Retorna o utilizador autenticado
                } else {
                    System.out.println("Credenciais inválidas.");
                    return null; // Senha incorreta
                }
            }
        }

        System.out.println("Nome de utilizador não encontrado.");
        return null;
    }

    
    // Método para salvar um utilizador e atualizar o cache
    public static void salvarUtilizador(Utilizador utilizador, Criptografia criptografia) {
        try (FileOutputStream fos = new FileOutputStream(ARQUIVO_UTILIZADORES, true);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos))) {

            // Serializa o objeto Utilizador
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(utilizador);
            }

            // Criptografa os dados serializados usando a instância passada
            String dadosSerializados = Base64.getEncoder().encodeToString(baos.toByteArray());
            String dadosCriptografados = criptografia.criptografar(dadosSerializados);

            // Escreve os dados criptografados no arquivo
            writer.write(dadosCriptografados);
            writer.newLine();

            System.out.println("Utilizador salvo com sucesso: " + utilizador.getNomeDeUtilizador());
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar utilizador no arquivo: " + e.getMessage(), e);
        }
    }

    
    public static void salvarTodoUtilizador(List<Utilizador> utilizadores, Criptografia criptografia) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_UTILIZADORES))) {
            for (Utilizador utilizador : utilizadores) {
                // Serializa o objeto Utilizador
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                    oos.writeObject(utilizador);
                }

                // Criptografa os dados serializados
                String dadosSerializados = Base64.getEncoder().encodeToString(baos.toByteArray());
                String dadosCriptografados = criptografia.criptografar(dadosSerializados);

                // Escreve os dados criptografados no arquivo
                writer.write(dadosCriptografados);
                writer.newLine();
            }
            System.out.println("Todos os utilizadores foram salvos com sucesso.");
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar utilizadores no arquivo: " + e.getMessage(), e);
        }
    }

    
    
    // Método para remover um utilizador e atualizar o cache
    public void removerUtilizador(Utilizador utilizador) {
        List<Utilizador> utilizadores = carregarUtilizadores(criptografia);
        utilizadores.removeIf(u -> u.getNomeDeUtilizador().equalsIgnoreCase(utilizador.getNomeDeUtilizador()));

        // Regrava o arquivo com a lista de utilizadores atualizada
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_UTILIZADORES))) {
            for (Utilizador u : utilizadores) {
                salvarUtilizador(u, criptografia); // Regrava cada utilizador
            }
            // Remove o nome do utilizador do cache
            removerCacheNomeDeUtilizador(utilizador.getNomeDeUtilizador());

        } catch (IOException e) {
            throw new RuntimeException("Erro ao remover utilizador: " + e.getMessage(), e);
        }
    }    
    
    
    public static Utilizador cadastroDeUtilizador(List<Utilizador> utilizadores, Criptografia criptografia) {
        Scanner menu = new Scanner(System.in);

        System.out.println("=== Cadastro de Utilizador ===");

        // Nome Completo
        String nomeCompleto;
        while (true) {
            System.out.print("Digite o seu Nome Completo: ");
            nomeCompleto = menu.nextLine();
            try {
                nomeCompleto = formatarNomeCompleto(nomeCompleto);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        // Nome de Utilizador
        String nomeDeUtilizador;
        while (true) {
            System.out.print("Digite o seu Nome de Utilizador: ");
            nomeDeUtilizador = menu.nextLine().trim();
            if (nomeDeUtilizadorUnico(nomeDeUtilizador, criptografia)){
                break; // Nome de utilizador único, sai do loop
            } else {
                System.out.println("Nome de utilizador já existe! Por favor, escolha outro. ");
            }
        }

        // Email
        String email;
        while (true) {
            System.out.print("Digite o seu email: ");
            email = menu.nextLine().trim();
            if (isEmailValido(email)) {
                break;
            } else {
                System.out.println("Email inválido! Por favor, digite um email válido.");
            }
        }

        // Password
        System.out.print("Digite sua senha: ");
        String password = menu.nextLine();

        // Privilégio
        Privilegio privilegio = null;
        while (privilegio == null) {
            System.out.println("Selecione um tipo de utilizador:");
            for (Privilegio p : Privilegio.values()) {
                System.out.println(p.getCodigoPrivilegio() + ". " + p.name());
            }
            System.out.print("Digite o número da opção correspondente: ");
            try {
                int opcaoPrivilegio = Integer.parseInt(menu.nextLine());
                privilegio = Privilegio.getFromCodigo(opcaoPrivilegio);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida! Por favor, digite um número.");
            }
        }

        // Criar e salvar o utilizador
        Utilizador utilizador = new Utilizador(nomeCompleto, nomeDeUtilizador, email, password, privilegio.name(), criptografia);
        utilizadores.add(utilizador);
        utilizador.salvarUtilizador(utilizador, criptografia); // Salva o utilizador no arquivo
        
        System.out.println("Utilizador cadastrado com sucesso!");
        return utilizador;
    }
    
    
    // Método para formatar o nome completo
    public static String formatarNomeCompleto(String nomeCompleto) {
        // Valida se o nome completo não está vazio e contém pelo menos 2 palavras
        if (nomeCompleto == null || nomeCompleto.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome completo não pode ser vazio.");
        }

        String[] partes = nomeCompleto.trim().split("\\s+"); // Divide o nome em partes por espaços

        if (partes.length < 2) {
            throw new IllegalArgumentException("O nome completo deve conter pelo menos um nome e um sobrenome.");
        }

        // Formata cada parte do nome, incluindo suporte a apóstrofos e hífens
        StringBuilder nomeFormatado = new StringBuilder();
        for (String parte : partes) {
            if (!parte.matches("[a-zA-ZÀ-ÿ'-]+")) {
                throw new IllegalArgumentException("Nome contém caracteres inválidos: " + parte);
            }
            nomeFormatado.append(parte.substring(0, 1).toUpperCase()) // Primeira letra em maiúscula
                         .append(parte.substring(1).toLowerCase())   // Restante da palavra em minúsculas
                         .append(" ");                              // Adiciona espaço entre as palavras
        }

        return nomeFormatado.toString().trim(); // Retorna o nome formatado
    }
    
    
    // Método para carregar os nomes de utilizador em cache
    private static void carregarCacheNomesDeUtilizador(Criptografia criptografia) {
        cacheNomesDeUtilizador.clear(); // Limpa o cache antes de recarregar
        File arquivo = new File(ARQUIVO_UTILIZADORES);

        if (!arquivo.exists()) {
            return; // Se o arquivo não existir, o cache permanecerá vazio
        }

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String dadosDescriptografados = criptografia.descriptografar(linha);
                byte[] dadosDeserializados = Base64.getDecoder().decode(dadosDescriptografados);
                try (ByteArrayInputStream bais = new ByteArrayInputStream(dadosDeserializados);
                     ObjectInputStream ois = new ObjectInputStream(bais)) {
                    Utilizador utilizador = (Utilizador) ois.readObject();
                    cacheNomesDeUtilizador.add(utilizador.getNomeDeUtilizador().toLowerCase());
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Erro ao carregar nomes de utilizadores: " + e.getMessage(), e);
        }
    }

    // Método atualizado para verificar unicidade
    public static boolean nomeDeUtilizadorUnico(String nomeDeUtilizador, Criptografia criptografia) {
        if (cacheNomesDeUtilizador.isEmpty()) {
            carregarCacheNomesDeUtilizador(criptografia); // Carrega o cache apenas se estiver vazio
        }
        return !cacheNomesDeUtilizador.contains(nomeDeUtilizador.toLowerCase()); // Verifica se o nome de utilizador é único
    }

    // Método para atualizar o cache ao adicionar um novo utilizador
    public static void atualizarCacheNomesDeUtilizador(String nomeDeUtilizador) {
        cacheNomesDeUtilizador.add(nomeDeUtilizador.toLowerCase()); // Adiciona o nome de utilizador ao cache
    }

    // Método para remover o nome de utilizador do cache
    public static void removerCacheNomeDeUtilizador(String nomeDeUtilizador) {
        cacheNomesDeUtilizador.remove(nomeDeUtilizador.toLowerCase()); // Remove o nome de utilizador do cache
    }
    
    
    // Verifica se o email é válido
    public static boolean isEmailValido(String email) {
        String regex = "^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(regex);
    }
    
    
    // Método para obter a senha com ocultação e validação de senha forte
    public static String obterPass(Scanner menu) {
        Console console = System.console();
        String password = null;
        if (console != null) {
            // Usar Console para ler a senha de forma oculta
            char[] senha = console.readPassword("Digite a sua password (mínimo 8 caracteres, uma letra maiúscula, uma minúscula, um número e um caractere especial): ");
            password = new String(senha);
        } else {
            // Console não disponível, usar Scanner
            System.out.print("Digite a sua password (mínimo 8 caracteres, uma letra maiúscula, uma minúscula, um número e um caractere especial): ");
            password = menu.nextLine();
        }

        // Validar se a senha é forte
        while (!validarPassForte(password)) {
            System.out.println("Senha fraca! A senha deve atender aos seguintes requisitos:");
            System.out.println("- Mínimo de 8 caracteres");
            System.out.println("- Pelo menos uma letra maiúscula");
            System.out.println("- Pelo menos uma letra minúscula");
            System.out.println("- Pelo menos um número");
            System.out.println("- Pelo menos um caractere especial (@, #, $, %, etc.)");
            System.out.print("Digite uma senha válida: ");
            password = menu.nextLine();
        }

        // Exibir a senha como "******"
        System.out.println("Senha cadastrada com sucesso!");

        return password;
    }
    
    // Método para validar se a senha é forte
    public static boolean validarPassForte(String senha) {
        // Requisito 1: Mínimo de 8 caracteres
        if (senha.length() < 8) {
            return false;
        }

        // Requisito 2: Pelo menos uma letra maiúscula
        Pattern maiuscula = Pattern.compile("[A-Z]");
        Matcher matcherMaiuscula = maiuscula.matcher(senha);
        if (!matcherMaiuscula.find()) {
            return false;
        }

        // Requisito 3: Pelo menos uma letra minúscula
        Pattern minuscula = Pattern.compile("[a-z]");
        Matcher matcherMinuscula = minuscula.matcher(senha);
        if (!matcherMinuscula.find()) {
            return false;
        }

        // Requisito 4: Pelo menos um número
        Pattern numero = Pattern.compile("[0-9]");
        Matcher matcherNumero = numero.matcher(senha);
        if (!matcherNumero.find()) {
            return false;
        }

        // Requisito 5: Pelo menos um caractere especial
        Pattern especial = Pattern.compile("[@#$%^&+=!]");
        Matcher matcherEspecial = especial.matcher(senha);
        return matcherEspecial.find();
    }

    boolean isPromotor() {
        return "Promotor".equalsIgnoreCase(privilegio);
    }
    
    
    // Classe enum para representar o privilégio do utilizador
    public enum Privilegio {

        PROMOTOR(1), // Privilégio de Promotor
        
        ATLETA(2), // Privilégio de Atleta
        
        LIDER_DE_EQUIPA(3), // Privilégio de Líder de Equipa
        
        ESPECTADOR(4); // Privilégio de Espetador


        private final int codigoPrivilegio; // Código associado ao privilégio

        // Construtor do enum
        Privilegio(int codigoPrivilegio) {
            this.codigoPrivilegio = codigoPrivilegio;
        }

        // Método para obter o código do privilégio
        public int getCodigoPrivilegio() {
            return codigoPrivilegio;
        }

        // Método estático para obter um privilégio a partir de um código
        public static Privilegio getFromCodigo(int codigoPrivilegio) {
            for (Privilegio privilegio : values()) {
                // Verifica se o código fornecido corresponde a um dos privilégios
                if (privilegio.codigoPrivilegio == codigoPrivilegio) {
                    return privilegio; // Retorna o privilégio correspondente
                }
            }
            return null; // Retorna null se o código não corresponder a nenhum privilégio
        }
    }
    

    @Override
    public String toString() {
        return "Utilizador{"
                + nomeCompleto + '\'' +
                 privilegio + '\'' +
                '}';
    }
}

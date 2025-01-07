/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ataque.arena.espacesistem;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author RyanS
 */
public class Evento implements Serializable {
    private static final long serialVersionUID = 1L; // Define o serialVersionUID explicitamente
    static final String ARQUIVO_EVENTOS = "eventos.dat"; // Caminho do arquivo onde os eventos serão armazenados
    private String nome;
    private final Promotor promotor; // Referência ao objeto Promotor
    private final Sala sala;
    private String modalidade;
    private LocalDateTime inicio;
    private LocalDateTime fim;
    private final int participantes;
    private final TipoEvento tipoEvento;
    double valorBase;
    double valorFinal;
    private final List<String> equipesInscritas = new ArrayList<>();

public Evento(String nome, Promotor promotor, Sala sala, String modalidade, LocalDateTime inicio, LocalDateTime fim, int participantes) {
    this.nome = nome;
    this.promotor = promotor; // Armazena o promotor diretamente
    this.sala = sala;
    this.modalidade = modalidade;
    this.inicio = inicio;
    this.fim = fim;
    this.participantes = participantes;
    this.tipoEvento = sala.determinarTipoEvento(participantes);
    calcularValorEvento(); // Chama o método de cálculo do valor
}



// Getters e setters (se necessário)
public Promotor getPromotor() {
    return promotor;
}

public String getNome() {
    return nome;
}

public void setNome(String nome) {
    this.nome = nome;
}

public String getModalidade() {
    return modalidade;
}

public void setModalidade(String modalidade) {
    this.modalidade = modalidade;
}

public LocalDateTime getInicio() {
    return inicio;
}

public void setInicio(LocalDateTime inicio) {
    this.inicio = inicio;
}

public LocalDateTime getFim() {
    return fim;
}

public void setFim(LocalDateTime fim) {
    this.fim = fim;
}

public double getValorBase() {
    return valorBase;
}

public void setValorBase(double valorBase) {
    this.valorBase = valorBase;
}

public TipoEvento getTipoEvento() {
    return tipoEvento;
}

public double getValorFinal() {
    return valorFinal;
}

public List<String> getEquipesInscritas() {
    return equipesInscritas;
}

public void adicionarEquipe(String equipe) {
    equipesInscritas.add(equipe);
}
    

    public String listarEquipes() {
        if (equipesInscritas.isEmpty()) {
            return "Nenhuma equipe inscrita.";
        }
        return String.join(", ", equipesInscritas);
    }
    
    
    public enum TipoEvento {
        PEQUENO, GRANDE, EXCLUSIVO
    }

    // Método que pode ser sobrescrito nas subclasses
    protected void calcularValorEvento() {
        switch (tipoEvento) {
            case PEQUENO -> {
                this.valorBase = 500; // Valor para eventos pequenos
                this.valorFinal = valorBase;
            }
            case EXCLUSIVO -> {
                this.valorBase = 5000; // Valor para eventos exclusivos
                this.valorFinal = valorBase;
            }
            case GRANDE -> {
                this.valorBase = 1000; // Valor base para eventos grandes
                this.valorFinal = valorBase; // Sobrescrever o valor final conforme necessidade
            }
        }
    }
    


    public static void salvarEvento(Evento evento) {
    try (FileOutputStream fos = new FileOutputStream(ARQUIVO_EVENTOS, true);
        ObjectOutputStream oos = new ObjectOutputStream(fos)) {
        oos.writeObject(evento);
    } catch (IOException e) {
        throw new RuntimeException("Erro ao salvar evento '" + evento.getNome() + "': " + e.getMessage());
    }
}


    public static List<Evento> carregarEventosDoPromotor(Promotor promotor) {
        List<Evento> eventos = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(Evento.ARQUIVO_EVENTOS);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (true) {
                try {
                    Evento evento = (Evento) ois.readObject();
                    if (evento.getPromotor().equals(promotor)) { // Filtrar eventos do promotor
                        eventos.add(evento);
                    }
                } catch (EOFException e) {
                    break; // Fim do arquivo alcançado
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar eventos: " + e.getMessage());
        }
        return eventos;
    }
    
    public static List<Evento> carregarTodosOsEventos() {
        List<Evento> eventos = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(ARQUIVO_EVENTOS);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (true) {
                try {
                    Evento evento = (Evento) ois.readObject();
                    eventos.add(evento); // Adicionar todos os eventos à lista
                } catch (EOFException e) {
                    break; // Fim do arquivo alcançado
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar eventos: " + e.getMessage());
        }
        return eventos;
    }




    
    // Método para ler e validar data
    private static LocalDateTime lerDataHora(Scanner scanner, String mensagem) {
        while (true) {
            try {
                System.out.print(mensagem);
                String dataStr = scanner.nextLine();
                return LocalDateTime.parse(dataStr, DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
            } catch (DateTimeParseException e) {
                System.out.println("Formato inválido. Por favor, use o formato dd-MM-yyyy HH:mm.");
            }
        }
    }


    // Exemplo de uso na criação de eventos
    public static void criarEvento(Scanner scanner, Promotor promotor) {
    System.out.println("=== Criar Novo Evento ===");
    System.out.print("Nome do Evento: ");
    String nome = scanner.nextLine();

    // Escolher a modalidade
    System.out.println("Escolha a Modalidade do Evento:");
    System.out.println("1 - CS-2");
    System.out.println("2 - League of Legends");
    System.out.println("3 - Valorant");
    System.out.print("Escolha uma opção: ");
    int modalidadeEscolhida = scanner.nextInt();
    scanner.nextLine(); // Consumir quebra de linha

    String modalidade;
    switch (modalidadeEscolhida) {
        case 1 -> modalidade = "CS-2";
        case 2 -> modalidade = "League of Legends";
        case 3 -> modalidade = "Valorant";
        default -> {
            System.out.println("Modalidade inválida. Evento não criado.");
            return;
        }
    }

    // Usando o método de leitura de data com validação
    LocalDateTime inicio = lerDataHora(scanner, "Data e Hora de Início (dd-MM-yyyy HH:mm): ");
    LocalDateTime fim = lerDataHora(scanner, "Data e Hora de Fim (dd-MM-yyyy HH:mm): ");

    System.out.println("Escolha o tipo de evento:");
    System.out.println("1 - Evento Pequeno (Máximo 100 participantes)");
    System.out.println("2 - Evento Grande (Máximo 200 participantes)");
    System.out.println("3 - Evento Exclusivo (Participantes ilimitados)");
    System.out.print("Escolha uma opção: ");
    int tipoEventoEscolhido = scanner.nextInt();
    scanner.nextLine(); // Consumir quebra de linha

    int maxParticipantes;
    Sala sala;

    // Determinar o tipo de evento e a sala
    switch (tipoEventoEscolhido) {
        case 1 -> {
            maxParticipantes = 100;
            sala = new Sala("Sala 1", 100);
        }
        case 2 -> {
            maxParticipantes = 200;
            sala = new Sala("Sala 2", 200);
        }
        case 3 -> {
            maxParticipantes = Integer.MAX_VALUE;
            sala = new Sala("Sala 3", 500); // Máximo fictício da sala exclusiva
        }
        default -> {
            System.out.println("Opção inválida. Evento não criado.");
            return;
        }
    }

    System.out.print("Número de Participantes (Máximo " + maxParticipantes + "): ");
    int participantes = scanner.nextInt();
    scanner.nextLine(); // Consumir quebra de linha

    // Depuração dos valores
    System.out.println("DEBUG: Número de participantes fornecido: " + participantes);
    System.out.println("DEBUG: Capacidade máxima permitida: " + maxParticipantes);

    // Verificar se o número de participantes é compatível com o tipo de evento
    if (participantes <= 0 || participantes > maxParticipantes) {
        System.out.println("Erro: Número de participantes incompatível com a sala escolhida.");
        return;
    }

    try {
        // Criação do evento
        Evento novoEvento = new Evento(
                nome,
                promotor, // Objeto promotor com nome e e-mail
                sala, // Sala definida com base no tipo de evento
                modalidade,
                inicio,
                fim,
                participantes
        );

        // Salva o evento no promotor e no arquivo
        promotor.adicionarEvento(novoEvento); // Adiciona o evento à lista do promotor
        Evento.salvarEvento(novoEvento); // Salva o evento no arquivo
        System.out.println("Evento criado com sucesso!");
        QRcode.gerarQRCodeConsole(); // gera qrcode de acesso
    } catch (Exception e) {
        System.out.println("Erro ao criar evento: " + e.getMessage());
    }
}

      








    public static int lerNumeroParticipantes(Scanner scanner, int maxParticipantes) {
        int participantes = 0;
        while (true) {
            System.out.print("Número de Participantes (Máximo " + maxParticipantes + "): ");
            participantes = scanner.nextInt();
            scanner.nextLine(); // Consumir quebra de linha

            if (participantes <= maxParticipantes && participantes > 0) {
                break;
            } else {
                System.out.println("Número de participantes inválido. O valor deve ser entre 1 e " + maxParticipantes + ".");
            }
        }
        return participantes;
    }



    
    public static void editarEventoPorPromotor(Scanner scanner, Promotor promotor) {
        // Carregar todos os eventos criados pelo promotor
        List<Evento> eventosDoPromotor = Evento.carregarTodosOsEventos().stream().filter(evento -> evento.getPromotor().getEmail().equalsIgnoreCase(promotor.getEmail())).toList();

        System.out.println("=== Eventos Criados por " + promotor.getNomeCompleto() + " ===");

        // Verifica se há eventos associados ao promotor
        if (eventosDoPromotor.isEmpty()) {
            System.out.println("Nenhum evento encontrado para este promotor.");
            return;
        }

        // Lista os eventos do promotor
        for (int i = 0; i < eventosDoPromotor.size(); i++) {
            System.out.printf("%d - %s (Modalidade: %s)%n", i + 1, eventosDoPromotor.get(i).getNome(), eventosDoPromotor.get(i).getModalidade());
        }

        System.out.print("Escolha um evento pelo número: ");
        int escolha = scanner.nextInt();
        scanner.nextLine(); // Consumir quebra de linha

        if (escolha < 1 || escolha > eventosDoPromotor.size()) {
            System.out.println("Opção inválida.");
            return;
        }

        // Obter o evento selecionado
        Evento evento = eventosDoPromotor.get(escolha - 1);

        // Exibir menu para edição
        System.out.println("=== Editar Detalhes do Evento ===");
        System.out.println("1 - Nome");
        System.out.println("2 - Data de Início");
        System.out.println("3 - Data de Fim");
        System.out.println("0 - Sair");
        System.out.print("Escolha uma opção: ");
        int opcao = scanner.nextInt();
        scanner.nextLine(); // Consumir quebra de linha

        // Processar a escolha do usuário
        switch (opcao) {
            case 1 -> {
                System.out.print("Novo Nome: ");
                String novoNome = scanner.nextLine();
                evento.setNome(novoNome);
            }
            case 2 -> {
                System.out.print("Nova Data e Hora de Início (dd-MM-yyyy HH:mm): ");
                LocalDateTime novoInicio = lerDataHora(scanner, "Data e Hora de Início (dd-MM-yyyy HH:mm): ");
                evento.setInicio(novoInicio);
            }
            case 3 -> {
                System.out.print("Nova Data e Hora de Fim (dd-MM-yyyy HH:mm): ");
                LocalDateTime novoFim = lerDataHora(scanner, "Data e Hora de Fim (dd-MM-yyyy HH:mm): ");
                evento.setFim(novoFim);
            }
            case 0 -> {
                System.out.println("Edição cancelada.");
                return;
            }
            default -> System.out.println("Opção inválida.");
        }

        // Atualizar e salvar os eventos no arquivo
        List<Evento> todosOsEventos = Evento.carregarTodosOsEventos();

        // Substituir o evento no arquivo com base na referência
        for (int i = 0; i < todosOsEventos.size(); i++) {
            if (todosOsEventos.get(i).equals(evento)) { // Substituir pela referência ou critérios robustos
                todosOsEventos.set(i, evento); // Atualiza o evento na lista
                break;
            }
        }
        Evento.salvarTodosOsEventos(todosOsEventos);
        System.out.println("Evento editado com sucesso!");
    }


    
    public static void excluirEventoPorPromotor(Scanner scanner, Promotor promotor) {
        // Carregar todos os eventos associados ao promotor
        List<Evento> eventos = Evento.carregarTodosOsEventos();

        // Filtrar eventos criados pelo promotor
        List<Evento> eventosDoPromotor = eventos.stream().filter(evento -> evento.getPromotor().getEmail().equalsIgnoreCase(promotor.getEmail())).toList();

        System.out.println("=== Excluir Eventos Criados por " + promotor.getNomeCompleto() + " ===");

        if (eventosDoPromotor.isEmpty()) {
            System.out.println("Você não possui eventos criados.");
            return;
        }

        // Listar os eventos do promotor
        for (int i = 0; i < eventosDoPromotor.size(); i++) {
            System.out.printf("%d - %s (Modalidade: %s)%n", i + 1, eventosDoPromotor.get(i).getNome(), eventosDoPromotor.get(i).getModalidade());
        }

        System.out.print("Escolha um evento pelo número para excluir: ");
        int escolha = scanner.nextInt();
        scanner.nextLine(); // Consumir quebra de linha

        if (escolha < 1 || escolha > eventosDoPromotor.size()) {
            System.out.println("Opção inválida.");
            return;
        }

        // Selecionar o evento a ser excluído
        Evento eventoParaExcluir = eventosDoPromotor.get(escolha - 1);

        // Remover o evento da lista geral de eventos
        eventos.remove(eventoParaExcluir);

        // Salvar a lista atualizada de eventos no arquivo
        salvarTodosOsEventos(eventos);
        System.out.println("Evento excluído com sucesso!");
    }




    
    
    private static void salvarTodosOsEventos(List<Evento> eventos) {
        try (FileOutputStream fos = new FileOutputStream(ARQUIVO_EVENTOS, false);
            ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            for (Evento evento : eventos) {
                oos.writeObject(evento);
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar todos os eventos: " + e.getMessage());
        }
    }

    //ja esta
    
    public static void listarEventosPorModalidade(Scanner scanner) {
        System.out.println("=== Listar Eventos por Modalidade ===");
        System.out.println("Escolha a Modalidade:");
        System.out.println("1 - CS-2");
        System.out.println("2 - League of Legends");
        System.out.println("3 - Valorant");
        System.out.print("Escolha uma opção: ");
        int modalidadeEscolhida = scanner.nextInt();
        scanner.nextLine(); // Consumir quebra de linha

        // Modalidade selecionada
        String modalidade;
        switch (modalidadeEscolhida) {
            case 1 -> modalidade = "CS-2";
            case 2 -> modalidade = "League of Legends";
            case 3 -> modalidade = "Valorant";
            default -> {
                System.out.println("Modalidade inválida.");
                return; // Sai do método se a modalidade for inválida
            }
        }

        // Carregar todos os eventos
        List<Evento> eventos = Evento.carregarTodosOsEventos();

        // Filtrar eventos pela modalidade
        List<Evento> eventosPorModalidade = eventos.stream()
                .filter(evento -> evento.getModalidade().equalsIgnoreCase(modalidade))
                .toList();

        // Exibir os eventos filtrados
        System.out.println("=== Eventos na Modalidade: " + modalidade + " ===");
        if (eventosPorModalidade.isEmpty()) {
            System.out.println("Nenhum evento encontrado na modalidade selecionada.");
        } else {
            eventosPorModalidade.forEach(evento -> {
                System.out.println("---------------------------");
                System.out.println("Nome: " + evento.getNome());
                System.out.println("Promotor: " + evento.getPromotor().getNomeDeUtilizador());
                System.out.println("Data de Início: " + evento.getInicio());
                System.out.println("Data de Fim: " + evento.getFim());
                System.out.println("Valor Final: € " + String.format("%.2f", evento.getValorFinal()));
                System.out.println("---------------------------");
            });
        }
    }






        
    
    
    public static void listarEventosDePromotor(Promotor promotor) {
        // Validar se o promotor não é nulo
        if (promotor == null) {
            System.out.println("Erro: Promotor não especificado.");
            return;
        }
        // Carregar todos os eventos
        List<Evento> eventos = Evento.carregarTodosOsEventos();
        
        // Filtrar eventos pela modalidade
        List<Evento> eventosPorPromotor = eventos.stream().filter(evento -> evento.getPromotor().getEmail().equalsIgnoreCase(promotor.getEmail())).toList();
        
        System.out.println("=== Eventos Criados por " + promotor.getNomeCompleto() + " ===");

        // Verifica se há eventos associados ao promotor
        if (eventosPorPromotor.isEmpty()) {
            System.out.println("Nenhum evento encontrado pertencente a este promotor.");
        } else {
            // Lista os eventos do promotor
            eventosPorPromotor.forEach(evento -> {
                System.out.println("---------------------------");
                System.out.println("Nome: " + evento.getNome());
                System.out.println("Modalidade: " + evento.getModalidade());
                System.out.println("Data de Início: " + evento.getInicio());
                System.out.println("Data de Fim: " + evento.getFim());
                System.out.println("Valor Final: € " + String.format("%.2f", evento.getValorFinal()));
                QRcode.gerarQRCodeConsole();
                System.out.println("---------------------------");
            });
        }
    }

   
    public static void criarEquipe(Scanner scanner, LiderEquipa lider, List<Utilizador> utilizadores) {
        System.out.println("=== Criar Nova Equipe ===");
        System.out.print("Nome da Equipe: ");
        String nomeEquipe = scanner.nextLine();

        // Lista para armazenar os jogadores da equipe
        List<Jogador> jogadoresEquipe = new ArrayList<>();

        // Filtrar apenas os jogadores disponíveis do sistema
        List<Jogador> jogadoresDisponiveis = utilizadores.stream()
                .filter(utilizador -> utilizador instanceof Jogador)
                .map(utilizador -> (Jogador) utilizador)
                .toList();

        if (jogadoresDisponiveis.isEmpty()) {
            System.out.println("Nenhum jogador disponível para adicionar à equipe.");
            return;
        }

        while (true) {
            System.out.println("=== Jogadores Disponíveis ===");
            for (int i = 0; i < jogadoresDisponiveis.size(); i++) {
                Jogador jogador = jogadoresDisponiveis.get(i);
                System.out.printf("%d - %s (%s)%n", i + 1, jogador.getNomeCompleto(), jogador.getNomeDeUtilizador());
            }

            System.out.print("Escolha um jogador pelo número (ou 0 para finalizar): ");
            int escolha = scanner.nextInt();
            scanner.nextLine(); // Consumir quebra de linha

            if (escolha == 0) {
                break; // Finaliza a seleção de jogadores
            }

            if (escolha < 1 || escolha > jogadoresDisponiveis.size()) {
                System.out.println("Opção inválida. Tente novamente.");
            } else {
                Jogador jogadorSelecionado = jogadoresDisponiveis.get(escolha - 1);
                jogadoresEquipe.add(jogadorSelecionado);
                jogadoresDisponiveis.remove(jogadorSelecionado); // Remove da lista de disponíveis
                System.out.println("Jogador " + jogadorSelecionado.getNomeCompleto() + " adicionado à equipe.");
            }
        }

        if (jogadoresEquipe.isEmpty()) {
            System.out.println("Nenhum jogador foi adicionado à equipe. Equipe não será criada.");
            return;
        }

        salvarEquipe(nomeEquipe, jogadoresEquipe, lider); // Salvar equipe associada ao líder
        System.out.println("Equipe " + nomeEquipe + " criada com sucesso!");
    }

    
    private static void salvarEquipe(String nomeEquipe, List<Jogador> jogadoresEquipe, LiderEquipa lider) {
        try (FileOutputStream fos = new FileOutputStream("equipes.dat", true);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(new Object[] { nomeEquipe, jogadoresEquipe, lider });
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar equipe: " + e.getMessage());
        }
    }
    
    
    public static void listarEquipas(LiderEquipa lider) {
        try (FileInputStream fis = new FileInputStream("equipes.dat");
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            System.out.println("=== Equipes Criadas por " + lider.getNomeCompleto() + " ===");
            boolean encontrouEquipe = false;

            while (true) {
                try {
                    Object[] equipeData = (Object[]) ois.readObject();
                    String nomeEquipe = (String) equipeData[0];
                    List<Jogador> jogadoresEquipe = (List<Jogador>) equipeData[1];
                    LiderEquipa liderEquipe = (LiderEquipa) equipeData[2];

                    // Verifica se a equipe pertence ao líder fornecido
                    if (lider.getEmail().equalsIgnoreCase(liderEquipe.getEmail())) {
                        encontrouEquipe = true;
                        System.out.println("Equipe: " + nomeEquipe);
                        System.out.println("Jogadores:");
                        for (Jogador jogador : jogadoresEquipe) {
                            System.out.println(" - " + jogador.getNomeCompleto() + " (" + jogador.getNomeDeUtilizador() + ")");
                        }
                        System.out.println("---------------------------");
                    }
                } catch (EOFException e) {
                    break; // Fim do arquivo
                }
            }

            if (!encontrouEquipe) {
                System.out.println("Nenhuma equipe encontrada para o líder " + lider.getNomeCompleto() + ".");
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao listar equipes: " + e.getMessage());
        }
    }



}

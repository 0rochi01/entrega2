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
    static final String ARQUIVO_EVENTOS = "eventos.dat"; // Caminho do arquivo onde os eventos serão armazenados
    private String nome;
    private final Promotor emailPromotor;
    private final Sala sala;
    private String modalidade;
    private LocalDateTime inicio;
    private LocalDateTime fim;
    private final int participantes;
    private final TipoEvento tipoEvento;
    double valorBase;
    double valorFinal;
    private final List<String> equipesInscritas = new ArrayList<>();

    public Evento(String nome, Promotor emailPromotor, Sala sala, String modalidade, LocalDateTime inicio, LocalDateTime fim, int participantes) {
        this.nome = nome;
        this.emailPromotor = emailPromotor;
        this.sala = sala;
        this.modalidade = modalidade;
        this.inicio = inicio;
        this.fim = fim;
        this.participantes = participantes;
        this.tipoEvento = sala.determinarTipoEvento(participantes);
        calcularValorEvento(); // Chama o método de cálculo do valor
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
    
    public Promotor getPromotor() {
        return emailPromotor;
    }
    

    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }

    public double getValorFinal() {
        return valorFinal;
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


    public static List<Evento> carregarEventos() {
        List<Evento> eventos = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(ARQUIVO_EVENTOS);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (true) {
                try {
                    eventos.add((Evento) ois.readObject());
                } catch (EOFException e) {
                    break; // Fim do arquivo alcançado
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Erro ao carregar eventos: " + e.getMessage());
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

        System.out.print("Modalidade do Evento: ");
        String modalidade = scanner.nextLine();

        // Usando o método de leitura de data com validação
        LocalDateTime inicio = lerDataHora(scanner, "Data e Hora de Início (dd-MM-yyyy HH:mm): ");
        LocalDateTime fim = lerDataHora(scanner, "Data e Hora de Fim (dd-MM-yyyy HH:mm): ");

        // Escolher o tipo de evento
        System.out.println("Escolha o tipo de evento:");
        System.out.println("1 - Evento Pequeno (Máximo 100 participantes)");
        System.out.println("2 - Evento Grande (Máximo 200 participantes)");
        System.out.println("3 - Evento Exclusivo (Participantes ilimitados)");
        System.out.print("Escolha uma opção: ");
        int tipoEventoEscolhido = scanner.nextInt();
        scanner.nextLine(); // Consumir quebra de linha

        Evento novoEvento = null;
        int maxParticipantes = 0;  // Capacidade máxima de participantes

        try {
            switch (tipoEventoEscolhido) {
                case 1 -> // Evento Pequeno
                    maxParticipantes = 100;  // Capacidade máxima de participantes para eventos pequenos
                case 2 -> // Evento Grande
                    maxParticipantes = 200;  // Capacidade máxima de participantes para eventos grandes
                case 3 -> // Evento Exclusivo
                    maxParticipantes = Integer.MAX_VALUE;  // Participantes ilimitados para eventos exclusivos
                default -> {
                    System.out.println("Opção inválida. Evento não criado.");
                    return;
                }
            }

            // Leitura do número de participantes de acordo com o tipo de evento
            int participantes = lerNumeroParticipantes(scanner, maxParticipantes);

            // Criação do evento de acordo com o tipo
            switch (tipoEventoEscolhido) {
                case 1 -> // Evento pequeno - instância da classe EventoPequeno
                    novoEvento = new EventoPequeno(nome, promotor, modalidade, inicio, fim, participantes);
                case 2 -> // Evento grande - instância da classe EventoGrande
                    novoEvento = new EventoGrande(nome, promotor, modalidade, inicio, fim, participantes);
                case 3 -> {
                    Sala salaExclusiva = new Sala("Sala Exclusiva", 500); // Exemplo de sala para evento exclusivo
                    novoEvento = new Evento(nome, promotor, salaExclusiva, modalidade, inicio, fim, participantes);
                }
            }
            
            promotor.adicionarEvento(novoEvento);// Adiciona o evento ao promotor
            salvarEvento(novoEvento);// Salva o evento no arquivo
            System.out.println("Evento criado com sucesso!");
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



    
    public static void editarEvento(Scanner scanner, Promotor promotor) {
        List<Evento> eventos = carregarEventos(); // Carrega todos os eventos do sistema
        System.out.println("=== Editar Eventos ===");

        // Filtra os eventos criados pelo promotor logado
        List<Evento> eventosPromotor = eventos.stream()
                .filter(evento -> evento.getPromotor().equals(promotor))
                .toList();

        if (eventosPromotor.isEmpty()) {
            System.out.println("Você não possui eventos criados.");
            return;
        }

        // Lista eventos criados pelo promotor
        for (int i = 0; i < eventosPromotor.size(); i++) {
            System.out.printf("%d - %s%n", i + 1, eventosPromotor.get(i).getNome());
        }

        System.out.print("Escolha um evento pelo número: ");
        int eventoEscolhido = scanner.nextInt();
        scanner.nextLine(); // Consumir quebra de linha

        if (eventoEscolhido < 1 || eventoEscolhido > eventosPromotor.size()) {
            System.out.println("Opção inválida!");
            return;
        }

        Evento evento = eventosPromotor.get(eventoEscolhido - 1);

        System.out.println("=== Editar Detalhes do Evento ===");
        System.out.println("1 - Nome");
        System.out.println("2 - Modalidade");
        System.out.println("3 - Data e Hora de Início");
        System.out.println("4 - Data e Hora de Fim");
        System.out.println("0 - Sair");
        System.out.print("Escolha uma opção: ");
        int opcao = scanner.nextInt();
        scanner.nextLine(); // Consumir quebra de linha

        switch (opcao) {
            case 1 -> {
                System.out.print("Novo Nome: ");
                String novoNome = scanner.nextLine();
                evento.setNome(novoNome);
            }
            case 2 -> {
                System.out.print("Nova Modalidade: ");
                String novaModalidade = scanner.nextLine();
                evento.setModalidade(novaModalidade);
            }
            case 3 -> {
                System.out.print("Nova Data e Hora de Início (dd-MM-yyyy HH:mm): ");
                String novoInicioStr = scanner.nextLine();
                evento.setInicio(LocalDateTime.parse(novoInicioStr, DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
            }
            case 4 -> {
                System.out.print("Nova Data e Hora de Fim (dd-MM-yyyy HH:mm): ");
                String novoFimStr = scanner.nextLine();
                evento.setFim(LocalDateTime.parse(novoFimStr, DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
            }
            case 0 -> {
                System.out.println("Edição cancelada.");
                return;
            }
            default -> {
                System.out.println("Opção inválida.");
                return;
            }
        }

        evento.calcularValorEvento(); // Recalcula o valor do evento após a edição
        salvarTodosOsEventos(eventos); // Salva os eventos atualizados
        System.out.println("Evento editado com sucesso!");
    }


    
    public static void excluirEvento(Scanner scanner, Promotor promotor) {
        List<Evento> eventos = carregarEventos(); // Carrega todos os eventos do sistema
        System.out.println("=== Excluir Evento ===");

        // Filtra os eventos criados pelo promotor logado
        List<Evento> eventosPromotor = eventos.stream()
                .filter(evento -> evento.getPromotor().equals(promotor))
                .toList();

        if (eventosPromotor.isEmpty()) {
            System.out.println("Você não possui eventos criados para excluir.");
            return;
        }

        // Lista eventos criados pelo promotor
        for (int i = 0; i < eventosPromotor.size(); i++) {
            System.out.printf("%d - %s%n", i + 1, eventosPromotor.get(i).getNome());
        }

        System.out.print("Escolha um evento pelo número: ");
        int eventoEscolhido = scanner.nextInt();
        scanner.nextLine(); // Consumir quebra de linha

        if (eventoEscolhido < 1 || eventoEscolhido > eventosPromotor.size()) {
            System.out.println("Opção inválida!");
            return;
        }

        Evento eventoParaExcluir = eventosPromotor.get(eventoEscolhido - 1);
        eventos.remove(eventoParaExcluir); // Remove o evento do arquivo principal

        salvarTodosOsEventos(eventos); // Salva os eventos atualizados
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

    
    
    public static void listarEventosEmCurso() {
        List<Evento> todosEventos = carregarEventos(); // Carrega todos os eventos salvos
        LocalDateTime agora = LocalDateTime.now(); // Obtém a data e hora atual
        System.out.println("=== Eventos em Curso ===");

        List<Evento> eventosEmCurso = todosEventos.stream()
                .filter(evento -> evento.getInicio().isBefore(agora) && evento.getFim().isAfter(agora))
                .toList(); // Filtra eventos que estão em curso

        if (eventosEmCurso.isEmpty()) {
            System.out.println("Nenhum evento em curso no momento.");
        } else {
            eventosEmCurso.forEach(evento -> {
                System.out.println("---------------------------");
                System.out.println("Nome: " + evento.getNome());
                System.out.println("Promotor: " + evento.getPromotor().getNomeDeUtilizador());
                System.out.println("Modalidade: " + evento.getModalidade());
                System.out.println("Data de Início: " + evento.getInicio());
                System.out.println("Data de Fim: " + evento.getFim());
                System.out.println("Tipo: " + evento.getTipoEvento());
                System.out.println("---------------------------");
            });
        }
    }


    
    
    public static void listarEventosDePromotor(Promotor promotor) {
        List<Evento> todosEventos = carregarEventos(); // Carrega todos os eventos salvos
        System.out.println("=== Eventos Criados por " + promotor.getNomeDeUtilizador() + " ===");

        List<Evento> eventosPromotor = todosEventos.stream()
                .filter(evento -> evento.getPromotor().equals(promotor))
                .toList(); // Filtra os eventos do promotor

        if (eventosPromotor.isEmpty()) {
            System.out.println("Nenhum evento encontrado para este promotor.");
        } else {
            eventosPromotor.forEach(evento -> {
                System.out.println("---------------------------");
                System.out.println("Nome: " + evento.getNome());
                System.out.println("Modalidade: " + evento.getModalidade());
                System.out.println("Data de Início: " + evento.getInicio());
                System.out.println("Data de Fim: " + evento.getFim());
                System.out.println("Tipo: " + evento.getTipoEvento());
                System.out.println("---------------------------");
            });
        }
    }




    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String equipes = listarEquipes();
        return String.format("""
                ---------------------------
                Evento: %s
                Promotor: %s
                Sala: %s
                Modalidade: %s
                Início: %s
                Fim: %s
                Participantes: %d
                Tipo: %s
                Valor Final: %.2f
                Equipes Inscritas: %s
                ---------------------------
                """,
                nome,
                emailPromotor != null ? emailPromotor.getNomeDeUtilizador() : "N/A",
                sala != null ? sala.getNome() : "N/A",
                modalidade != null ? modalidade : "N/A",
                inicio.format(formatter),
                fim.format(formatter),
                participantes,
                tipoEvento != null ? tipoEvento : "N/A",
                valorFinal > 0 ? valorFinal : 0.0,
                equipes != null && !equipes.isEmpty() ? equipes : "Nenhuma");
    }

    
}
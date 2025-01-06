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
        try (FileInputStream fis = new FileInputStream(Evento.ARQUIVO_EVENTOS);
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

        // Definir a modalidade com a sintaxe tradicional do switch
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

        try {
            // Criação do evento
            Evento novoEvento = new Evento(
                    nome,
                    promotor, // Objeto promotor com nome e e-mail
                    new Sala("Sala Padrão", 200), // Exemplo de sala
                    modalidade,
                    inicio,
                    fim,
                    0 // Número de participantes inicial
            );

            // Salva o evento no promotor e no arquivo
            promotor.adicionarEvento(novoEvento); // Adiciona o evento à lista do promotor
            Evento.salvarEvento(novoEvento); // Salva o evento no arquivo
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



    
    public static void editarEventoPorPromotor(Scanner scanner, Promotor promotor) {
        List<Evento> eventos = carregarEventosDoPromotor(promotor);
        System.out.println("=== Editar Eventos ===");

        if (eventos.isEmpty()) {
            System.out.println("Você não possui eventos criados.");
            return;
        }

        for (int i = 0; i < eventos.size(); i++) {
            System.out.printf("%d - %s (Modalidade: %s)%n", i + 1, eventos.get(i).getNome(), eventos.get(i).getModalidade());
        }
        System.out.print("Escolha um evento pelo número: ");
        int escolha = scanner.nextInt();
        scanner.nextLine(); // Consumir quebra de linha

        if (escolha < 1 || escolha > eventos.size()) {
            System.out.println("Opção inválida.");
            return;
        }

        Evento evento = eventos.get(escolha - 1);

        System.out.println("=== Editar Detalhes do Evento ===");
        System.out.println("1 - Nome");
        System.out.println("2 - Data de Início");
        System.out.println("3 - Data de Fim");
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

        salvarTodosOsEventos(carregarTodosOsEventos()); // Salva todos os eventos no arquivo
        System.out.println("Evento editado com sucesso!");
    }




    
    public static void excluirEventoPorPromotor(Scanner scanner, Promotor promotor) {
        List<Evento> eventos = carregarEventosDoPromotor(promotor);
        System.out.println("=== Excluir Eventos ===");

        if (eventos.isEmpty()) {
            System.out.println("Você não possui eventos criados.");
            return;
        }

        for (int i = 0; i < eventos.size(); i++) {
            System.out.printf("%d - %s (Modalidade: %s)%n", i + 1, eventos.get(i).getNome(), eventos.get(i).getModalidade());
        }
        System.out.print("Escolha um evento pelo número: ");
        int escolha = scanner.nextInt();
        scanner.nextLine(); // Consumir quebra de linha

        if (escolha < 1 || escolha > eventos.size()) {
            System.out.println("Opção inválida.");
            return;
        }

        Evento evento = eventos.get(escolha - 1);
        eventos.remove(evento); // Remove o evento da lista

        salvarTodosOsEventos(carregarTodosOsEventos()); // Salva todos os eventos no arquivo
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
                System.out.println("Valor Final: " + evento.getValorFinal());
            });
        }
    }






        
    
    
    public static void listarEventosDePromotor(Promotor promotor) {
        // Obtém a lista de eventos criados pelo promotor
        List<Evento> eventosDoPromotor = promotor.getEventosCriados();
        System.out.println("=== Eventos Criados por " + promotor.getNomeCompleto() + " ===");

        // Verifica se há eventos associados ao promotor
        if (eventosDoPromotor.isEmpty()) {
            System.out.println("Nenhum evento encontrado para este promotor.");
        } else {
            // Lista os eventos do promotor
            eventosDoPromotor.forEach(evento -> {
                System.out.println("---------------------------");
                System.out.println("Nome: " + evento.getNome());
                System.out.println("Modalidade: " + evento.getModalidade());
                System.out.println("Data de Início: " + evento.getInicio());
                System.out.println("Data de Fim: " + evento.getFim());
                System.out.println("Valor Final: " + evento.getValorFinal());
            });
        }
    }







    




    
}
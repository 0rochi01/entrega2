/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ataque.arena.espacesistem;

import java.util.*;
import java.util.stream.Collectors;

public class Espectador extends Utilizador {
    private static final Map<String, List<Evento>> eventosInscritosPorEspectador = new HashMap<>();

    public Espectador(String nomeCompleto, String nomeDeUtilizador, String email, String password, String privilegio, Criptografia criptografia) {
        super(nomeCompleto, nomeDeUtilizador, email, password, privilegio, criptografia);
    }

    // Método estático para inscrever-se em um evento
    public static void inscreverNumEvento(Scanner scanner, Espectador espectador) {
        System.out.println("=== Listar Eventos por Modalidade ===");
        System.out.println("Escolha a Modalidade de desejo:");
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
            return;
        }

        for (int i = 0; i < eventosPorModalidade.size(); i++) {
            Evento evento = eventosPorModalidade.get(i);
            System.out.printf("%d - %s (Data de Início: %s, Data de Fim: %s)%n", i + 1,
                    evento.getNome(),
                    evento.getInicio(),
                    evento.getFim());
        }

        System.out.print("Escolha um evento (pelo número) para se inscrever:  ");
        int escolha = scanner.nextInt();
        scanner.nextLine(); // Consumir quebra de linha

        if (escolha < 1 || escolha > eventosPorModalidade.size()) {
            System.out.println("Opção inválida.");
            return;
        }

        Evento eventoEscolhido = eventosPorModalidade.get(escolha - 1);
        String emailEspectador = espectador.getEmail();

        eventosInscritosPorEspectador.putIfAbsent(emailEspectador, new ArrayList<>());
        List<Evento> eventosInscritos = eventosInscritosPorEspectador.get(emailEspectador);

        if (eventosInscritos.contains(eventoEscolhido)) {
            System.out.println("Você já está inscrito neste evento.");
        } else {
            eventosInscritos.add(eventoEscolhido);
            System.out.println("Inscrição realizada com sucesso no evento: " + eventoEscolhido.getNome());
            System.out.println("Não se esqueça de realizar pagamento no espaço físico para ter acesso ao evento presencial");
            QRcode.gerarQRCodeConsole();
        }
    }

    // Método estático para listar os eventos em que um espectador está inscrito
    public static void listarEventosInscritos(Espectador espectador) {
        String emailEspectador = espectador.getEmail();

        System.out.println("=== Eventos Inscritos por " + espectador.getNomeCompleto() + " ===");
        List<Evento> eventosInscritos = eventosInscritosPorEspectador.getOrDefault(emailEspectador, new ArrayList<>());

        if (eventosInscritos.isEmpty()) {
            System.out.println("Você ainda não está inscrito em nenhum evento.");
            return;
        }

        for (int i = 0; i < eventosInscritos.size(); i++) {
            Evento evento = eventosInscritos.get(i);
            System.out.printf("%d - %s (Modalidade: %s, Data de Início: %s, Data de Fim: %s)%n", i + 1,
                    evento.getNome(),
                    evento.getModalidade(),
                    evento.getInicio(),
                    evento.getFim());
        }
        QRcode.gerarQRCodeConsole();
    }
}



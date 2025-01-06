/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ataque.arena.espacesistem;

import java.time.LocalDateTime;

/**
 *
 * @author RyanS
 */

public class EventoGrande extends Evento {

    private static final Sala SALA_GRANDE = new Sala("Sala 2", 200); // Sala para eventos grandes

    public EventoGrande(String nome, Promotor emailPromotor, String modalidade, LocalDateTime inicio, LocalDateTime fim, int participantes) {
        super(nome, emailPromotor, SALA_GRANDE, modalidade, inicio, fim, participantes);
    }

    @Override
    protected void calcularValorEvento() {
        super.calcularValorEvento(); // Chama o cálculo da classe pai
        // Aplica o desconto de 10% para eventos grandes
        if (getTipoEvento() == TipoEvento.GRANDE) {
            valorFinal = valorBase * 0.90; // Aplica o desconto de 10%
        }
    }
}

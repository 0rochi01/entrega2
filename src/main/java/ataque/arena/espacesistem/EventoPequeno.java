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
public class EventoPequeno extends Evento {

    private static final Sala SALA_PEQUENO = new Sala("Sala Pequena", 100);

    public EventoPequeno(String nome, Promotor emailPromotor, String modalidade, LocalDateTime inicio, LocalDateTime fim, int participantes) {
        super(nome, emailPromotor, SALA_PEQUENO, modalidade, inicio, fim, participantes);
    }

    @Override
    protected void calcularValorEvento() {
        super.calcularValorEvento();  // Chama o cálculo da classe pai
        // Aqui podemos ajustar se necessário, por exemplo, modificar o valor base ou final
    }
}

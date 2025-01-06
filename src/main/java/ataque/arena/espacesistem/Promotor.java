/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ataque.arena.espacesistem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 *
 * @author RyanS
 */
public class Promotor extends Utilizador {

    private List<Evento> eventosCriados;

    public Promotor(String nomeCompleto, String nomeDeUtilizador, String email, String password, String privilegio, Criptografia criptografia) {
        super(nomeCompleto, nomeDeUtilizador, email, password, privilegio, criptografia);
        this.eventosCriados = new ArrayList<>();
    }

    public List<Evento> getEventosCriados() {
        return eventosCriados;
    }

    public void adicionarEvento(Evento evento) {
        eventosCriados.add(evento);
    }

    
    
    public enum OpcaoMenuPromotor {
        GERIR_EVENTOS(1),
        SAIR(0);

        private final int codigo;

        OpcaoMenuPromotor(int codigo) {
            this.codigo = codigo;
        }

        public int getCodigo() {
            return codigo;
        }

        // Método para obter uma opção a partir do código
        public static OpcaoMenuPromotor getFromCodigo(int codigo) {
            for (OpcaoMenuPromotor opcao : OpcaoMenuPromotor.values()) {
                if (opcao.getCodigo() == codigo) {
                    return opcao;
                }
            }
            return null; // Retorna null se o código não corresponder a nenhuma opção
        }
    }
    
    public String serializar() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(this);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Erro ao serializar o objeto: " + e.getMessage(), e);
        }
    }

    
    @Override
    public String toString() {
        return "Promotor{" +
               "nomeCompleto='" + getNomeCompleto() + '\'' +
               ", nomeDeUtilizador='" + getNomeDeUtilizador() + '\'' +
               ", email='" + getEmail() + '\'' +
               ", privilegio='" + getPrivilegio() + '\'' +
               '}';
    }
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ataque.arena.espacesistem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author RyanS
 */

public class Criptografia {

    private static final String ALGORITMO = "AES";
    private static final String ARQUIVO_CHAVE = "chave_secreta.key";
    private SecretKey chave;

    // Instância singleton
    private static Criptografia instancia;

    Criptografia() {
        carregarOuGerarChave();
    }

    // Obtém a instância única
    public static Criptografia getInstance() {
        if (instancia == null) {
            instancia = new Criptografia();
        }
        return instancia;
    }

    // Carrega a chave existente ou gera uma nova
    private void carregarOuGerarChave() {
        this.chave = carregarChave();
        if (this.chave == null) {
            this.chave = gerarChave();
            salvarChave();
        }
    }

    // Gera uma nova chave secreta
    private SecretKey gerarChave() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITMO);
            keyGen.init(256); // Tamanho da chave (256 bits)
            return keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao gerar chave secreta: " + e.getMessage(), e);
        }
    }

    // Salva a chave secreta em um arquivo
    private void salvarChave() {
        try (FileOutputStream fos = new FileOutputStream(ARQUIVO_CHAVE)) {
            fos.write(chave.getEncoded());
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar chave secreta: " + e.getMessage(), e);
        }
    }

    // Carrega a chave secreta de um arquivo
    private SecretKey carregarChave() {
        File arquivo = new File(ARQUIVO_CHAVE);
        if (!arquivo.exists()) {
            return null; // Retorna nulo se o arquivo não existir
        }

        try (FileInputStream fis = new FileInputStream(arquivo)) {
            byte[] chaveBytes = fis.readAllBytes();
            return new SecretKeySpec(chaveBytes, ALGORITMO);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar chave secreta: " + e.getMessage(), e);
        }
    }

    // Criptografa um texto
    public String criptografar(String texto) {
        validarChave();
        if (texto == null) {
        throw new IllegalArgumentException("O texto para criptografar não pode ser nulo.");
        }
        try {
            Cipher cipher = Cipher.getInstance(ALGORITMO);
            cipher.init(Cipher.ENCRYPT_MODE, chave);
            byte[] textoCriptografado = cipher.doFinal(texto.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(textoCriptografado);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criptografar texto: " + e.getMessage(), e);
        }
    }

    // Descriptografa um texto
    public String descriptografar(String textoCriptografado) {
        validarChave();
        try {
            Cipher cipher = Cipher.getInstance(ALGORITMO);
            cipher.init(Cipher.DECRYPT_MODE, chave);
            byte[] textoBytes = Base64.getDecoder().decode(textoCriptografado);
            byte[] textoDescriptografado = cipher.doFinal(textoBytes);
            return new String(textoDescriptografado, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao descriptografar texto: " + e.getMessage(), e);
        }
    }

    // Gera um hash de senha usando SHA-256
    public String gerarHashDeSenha(String senha) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(senha.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao gerar hash de senha: " + e.getMessage(), e);
        }
    }

    // Verifica se uma senha corresponde ao hash gerado
    public boolean verificarSenha(String senha, String hash) {
        String hashDaSenha = gerarHashDeSenha(senha);
        return hashDaSenha.equals(hash);
    }

    // Valida se a chave foi carregada corretamente
    private void validarChave() {
        if (chave == null) {
            throw new IllegalStateException("A chave secreta não está disponível.");
        }
    }

}

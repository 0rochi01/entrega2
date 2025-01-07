/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ataque.arena.espacesistem;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Random;

/**
 *
 * @author aigor
 */
public class QRcode {
    
        public QRcode() {
        gerarQRCodeConsole();
    }

    protected static void gerarQRCodeConsole() {
        String textoAleatorio = gerarTextoAleatorio();
        int largura = 20; // Largura do QR Code
        int altura = 20; // Altura do QR Code

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(textoAleatorio, BarcodeFormat.QR_CODE, largura, altura);
            System.out.println("QR Code gerado com o texto: " + textoAleatorio);

            // Exibir QR Code no console
            for (int y = 0; y < altura; y++) {
                for (int x = 0; x < largura; x++) {
                    System.out.print(bitMatrix.get(x, y) ? "██" : "  "); // "██" para preenchido, "  " para vazio
                }
                System.out.println();
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private static String gerarTextoAleatorio() {
        // Gera um texto aleatório (pode ser ajustado conforme necessário)
        int length = 10; // Tamanho do texto aleatório
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(caracteres.length());
            sb.append(caracteres.charAt(index));
        }
        return sb.toString();
    }
        
    }
    
    
    


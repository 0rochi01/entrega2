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
        gerarQRCodeAleatorio();
    }

    private void gerarQRCodeAleatorio() {
        String textoAleatorio = gerarTextoAleatorio();
        String caminho = "qrcode.png"; // Caminho onde o QR code será salvo

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(textoAleatorio, BarcodeFormat.QR_CODE, 200, 200);
            Path path = FileSystems.getDefault().getPath(caminho);
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
            System.out.println("QR Code gerado com o texto: " + textoAleatorio);
            System.out.println("QR Code salvo em: " + caminho);
        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }
    }

    private String gerarTextoAleatorio() {
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
    
    
    


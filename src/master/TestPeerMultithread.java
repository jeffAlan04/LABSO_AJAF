package master;

import java.io.*;
import java.net.*;

public class TestPeerMultithread {

    public static void main(String[] args) {
        int numeroPeer = 5; // Numero di peer simultanei
        int porta = 9000;

        for (int i = 1; i <= numeroPeer; i++) {
            int peerId = i;
            new Thread(() -> eseguiPeer(peerId, porta)).start();
        }
    }

    private static void eseguiPeer(int id, int porta) {
        try (Socket socket = new Socket("localhost", porta);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String risorsa = "R" + id;

            // Aggiungi una risorsa
            out.println("AGGIUNGI|" + risorsa);
            System.out.println("Peer " + id + ": " + in.readLine());

            // Richiedi lista risorse
            out.println("LISTA");
            System.out.println("Peer " + id + ": " + in.readLine());

            // Tutti i peer diversi da 1 scaricano R1
            if(id != 1){
                out.println("DOWNLOAD|R1");
                System.out.println("Peer " + id + ": " + in.readLine());
            }

            // Aspetta un momento per assicurarsi che tutti i download siano completati
            Thread.sleep(500);

            // Solo Peer 1 richiede il log alla fine
            if(id == 1) {
                out.println("LOG");
                String risposta;
                while ((risposta = in.readLine()) != null && !risposta.isEmpty()){
                    System.out.println("Peer " + id + " LOG: " + risposta);
                }
            }

            // Disconnetti il peer
            out.println("QUIT");
            System.out.println("Peer " + id + ": " + in.readLine());

        } catch (IOException | InterruptedException e) {
            System.err.println("Peer " + id + " errore: " + e.getMessage());
        }
    }
}

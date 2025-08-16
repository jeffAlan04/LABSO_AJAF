package master;

import java.io.*;
import java.net.*;

public class TestPeerMultithread {

    public static void main(String[] args) {
        int numeroPeer = 5; // Numero di peer simultanei
        int porta = 12345;

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

            // Disconnetti il peer
            out.println("QUIT");
            System.out.println("Peer " + id + ": " + in.readLine());

        } catch (IOException e) {
            System.err.println("Peer " + id + " errore: " + e.getMessage());
        }
    }
}

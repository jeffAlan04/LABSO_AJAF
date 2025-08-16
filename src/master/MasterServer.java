package master;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class MasterServer {
    // da cambiare con programma tabella anouar
    private final TabellaRisorse tabella = new TabellaRisorse();
    private final ExecutorService pool = Executors.newCachedThreadPool();

    public void avvia(int porta) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(porta)) {
            System.out.println("master in ascolto sulla porta " + porta);

            while (true) {
                Socket socketPeer = serverSocket.accept();
                System.out.println("nuovo peer connesso: " + socketPeer.getInetAddress());

                pool.execute(new GestorePeer(socketPeer, tabella));
            }
        }
    }

    // test da eliminare poi
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Uso corretto: java master.MasterServer <porta>");
            System.out.println("Esempio: java master.MasterServer 12345");
            return;
        }

        try {
            int porta = Integer.parseInt(args[0]);
            new MasterServer().avvia(porta);
        } catch (NumberFormatException e) {
            System.out.println("Errore: la porta deve essere un numero intero.");
        } catch (IOException e) {
            System.out.println("Errore durante l'avvio del server: " + e.getMessage());
        }
    }
}

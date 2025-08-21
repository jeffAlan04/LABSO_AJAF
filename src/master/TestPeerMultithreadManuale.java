package master;

import java.io.*;
import java.net.*;
import java.util.*;

// Classe che simula un peer manuale per testare il Master
public class TestPeerMultithreadManuale {

    public static void main(String[] args) {
        String host = "localhost"; // indirizzo del master
        int port = 9000;           // porta del master

        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            // Thread separato per leggere continuamente le risposte dal master
            Thread readerThread = new Thread(() -> {
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        if (!line.equals("END")) { // ignora linee "END" usate dal master come terminatori
                            System.out.println(line);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Connessione chiusa dal master.");
                }
            });
            readerThread.setDaemon(true); // termina automaticamente quando il main thread termina
            readerThread.start();

            // Loop principale per leggere comandi dall'utente e inviarli al master
            System.out.println("Inserisci comando (ADD <nome> <contenuto>, LISTDATA REMOTE, DOWNLOAD <risorsa>, LOG, QUIT):");
            while (true) {
                String cmd = scanner.nextLine().trim();
                if (cmd.isEmpty()) continue;

                out.println(cmd); // invia il comando al master
                out.flush();

                if (cmd.equalsIgnoreCase("QUIT")) { // chiusura del client
                    System.out.println("Chiusura client...");
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("Errore di connessione al master: " + e.getMessage());
        }
    }
}

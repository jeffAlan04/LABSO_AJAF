import java.util.*;
import java.io.*;
import java.net.*;

public class Master {

    private static Socket socket;
    private static GestioneTab tabella;
    private static ArbitroLetturaScrittura arbitroTabella;
    private static ArbitroLetturaScrittura arbitroLog;
    private static LogMaster logger;
    protected static List<GestionePeer> listaGestoriPeer = Collections.synchronizedList(new ArrayList<>());

    public static boolean inEsecuzione = true;

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Utilizzo: java Master <porta>");
            return;
        }

        // Porta su cui il server rimane in ascolto
        int porta = Integer.parseInt(args[0]);

        tabella = new GestioneTab();
        arbitroTabella = new ArbitroLetturaScrittura();
        arbitroLog = new ArbitroLetturaScrittura();
        logger = new LogMaster();

        // Creazione del ServerSocket in ascolto sulla porta indicata
        try (ServerSocket serverSocket = new ServerSocket(porta)) {

            System.out.println("Server in ascolto sulla porta: " + porta);

            // Avvio del thread di GestoreComandi
            new Thread(new GestoreComandi(arbitroLog, arbitroTabella, tabella, logger, serverSocket)).start();

            // Ciclo che accetta nuove connesioni fino a che inEsecuzione = true
            while (inEsecuzione) {

                // Accetta nuova connessione in ingresso
                socket = serverSocket.accept();

                // Per ogni peer connesso, si crea un oggetto GestionePeer
                GestionePeer gp = new GestionePeer(socket, logger, arbitroTabella, arbitroLog, tabella);
                synchronized (listaGestoriPeer) {
                    listaGestoriPeer.add(gp);
                }

                // Avvio del thread
                new Thread(gp).start();
            }
        }

        catch (IOException e) {
            System.out.println("Chiusura master.");
        }
    }
}

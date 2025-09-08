import java.io.*;
import java.net.*;

public class GestoreComandi implements Runnable{
    // arbitro per sincronizzare accesso ai log
    private final ArbitroLetturaScrittura arbitroLog;
    // arbitro per sincronizzare accesso alla tabella
    private final ArbitroLetturaScrittura arbitroTabella;
    // riferimento alla tabella delle risorse
    private final GestioneTab tabella;
    // Log per leggere i logging
    private final Log logger;
    private final ServerSocket serverSocket;

    public GestoreComandi(ArbitroLetturaScrittura arbitroLog, ArbitroLetturaScrittura arbitroTabella, GestioneTab tabella, Log logger, ServerSocket serverSocket) {
        this.arbitroLog = arbitroLog;
        this.arbitroTabella = arbitroTabella;
        this.tabella = tabella;
        this.logger = logger;
        this.serverSocket = serverSocket;
    }

    // Loop principale che legge i comandi dalla console
    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        ) {
            String messaggio;
            while ((messaggio = in.readLine()) != null) {
                messaggio = messaggio.trim();

                if ("LOG".equalsIgnoreCase(messaggio)) {
                    gestisciLog();
                } else if ("LISTDATA".equalsIgnoreCase(messaggio)) {
                    gestisciListData();
                } else if ("QUIT".equalsIgnoreCase(messaggio)) {
                    gestisciQuit();
                    break;
                }  else {
                    System.out.println("Comando non riconosciuto: " + messaggio);
                }
            }
        } catch (IOException e) {
            System.err.println("Errore nella lettura dei comandi: " + e.getMessage());
        }
    }
    //Gestisce il comando "Log" del master, Stampa su console il contenuto del file di log del master
    private void gestisciLog() {
        arbitroLog.inizioLettura();
            logger.stampa();
            arbitroLog.fineLettura();
    }

    // Gestisce il comando "listdata" del master, stampando tutte le risorse e i peer associati
    private void gestisciListData() {
        arbitroTabella.inizioLettura();
        try {
            System.out.println("=== Tabella Risorse ===");
            System.out.println(tabella.getRisorse());
        } finally {
            arbitroTabella.fineLettura();
        }
    }

    // Gestisce il comando "quit" del master, fermando l'esecuzione e chiudendo la JVM
    private void gestisciQuit() {
        System.out.println("Chiusura master in corso...");
        Master.inEsecuzione = false;
        synchronized (Master.listaGestoriPeer) {
            for (GestionePeer gp : Master.listaGestoriPeer) {
                gp.quit();
            }
        }
        try {
            this.serverSocket.close();
        }
        catch (IOException e) {
            System.out.println("Errore nella chiusura della server socket del master.");
        }
    }
}

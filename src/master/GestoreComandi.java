import java.io.*;

public class GestoreComandi implements Runnable{
    // arbitro per sincronizzare accesso ai log
    private final ArbitroLetturaScrittura arbitroLog;
    // arbitro per sincronizzare accesso alla tabella
    private final ArbitroLetturaScrittura arbitroTabella;
    // riferimento alla tabella delle risorse
    private final GestioneTab tabella;
    // Log per leggere i logging
    private final Master Log;

    public GestoreComandi(ArbitroLetturaScrittura arbitroLog, ArbitroLetturaScrittura arbitroTabella,GestioneTab tabella,Master Log) {
        this.arbitroLog = arbitroLog;
        this.arbitroTabella = arbitroTabella;
        this.tabella = tabella;
        this.Log = Log;
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

                if (messaggio.toUpperCase().startsWith("LOG")) {
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
        try {
            Log.stampa();
        } finally {
            arbitroLog.fineLettura();
        }
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
        System.exit(0);
    }
}

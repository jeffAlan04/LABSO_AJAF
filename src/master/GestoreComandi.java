package master;

import java.io.*;

public class GestoreComandi implements Runnable{
    private final ArbitroLetturaScrittura arbitroLog;
    private final ArbitroLetturaScrittura arbitroTabella;
    private final GestioneTab tabella;

    public GestoreComandi(ArbitroLetturaScrittura arbitroLog, ArbitroLetturaScrittura arbitroTabella,GestioneTab tabella) {
        this.arbitroLog = arbitroLog;
        this.arbitroTabella = arbitroTabella;
        this.tabella = tabella;
    }

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
                } else if (messaggio.equalsIgnoreCase("LISTDATA")) {
                    gestisciListData();
                } else if (messaggio.equalsIgnoreCase("QUIT")) {
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
            Log.getInstance().stampa();
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

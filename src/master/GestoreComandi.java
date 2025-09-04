package master;

import java.io.*;

public class GestoreComandi implements Runnable{
    private final ArbitroLetturaScrittura arbitroLog;
    private final ArbitroLetturaScrittura arbitroTabella;

    public GestoreComandi(ArbitroLetturaScrittura arbitroLog, ArbitroLetturaScrittura arbitroTabella) {
        this.arbitroLog = arbitroLog;
        this.arbitroTabella = arbitroTabella;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                PrintWriter out = new PrintWriter(System.out, true);
        ) {
            String messaggio;
            while ((messaggio = in.readLine()) != null) {
                messaggio = messaggio.trim();

                if (messaggio.toUpperCase().startsWith("LOG")) {
                    gestisciLog(out); // aggiungi risorsa
                } else if (messaggio.equalsIgnoreCase("LISTDATA REMOTE")) {
                    gestisciListData(out); // lista risorse remote
                } else if (messaggio.equalsIgnoreCase("QUIT")) {
                     // segnala disconnessione
                    out.println("Peer " + "(aggiungere nome peer)"  + " disconnesso");
                    //aggiungere comando per chiudere anche tutti i programmi del master
                    break;
                }  else {
                    out.println("Comando non riconosciuto: " + messaggio);
                }
            }
        } catch (IOException e) {
            System.err.println("Errore con peer " + "(aggiungere nome peer)" + ": " + e.getMessage());
        }
    }
    // Stampa su console il contenuto del file di log del master
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

}

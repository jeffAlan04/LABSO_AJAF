import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.File;

public class Logger {
    private PrintWriter scrittore;
    private String classe;
    private final String CARTELLA_LOG = ".log/";

    public Logger(String classe) {
        this.classe = classe; // nome della classe chiamante, così da rendere identificabili i log

        PrintWriter scrittoreTemporaneo;
        try {
            controlloEsistenzaCartellaLog();
            FileWriter f = new FileWriter(new File(generaNome()), true);
            scrittoreTemporaneo = new PrintWriter(f);
        } catch (IOException e) { // in caso di errori passa alla stampa su console
            System.out.println("Errore nella creazione del file di log. I log verranno stampati su console");
            scrittoreTemporaneo = new ConsolePrintWriter();
        }

        this.scrittore = scrittoreTemporaneo;
    }

    // controlla l'esistenza della classe per i log, eventualmente la crea
    private void controlloEsistenzaCartellaLog() {
        File cartella = new File(CARTELLA_LOG);
        if (!cartella.exists()) {
            cartella.mkdirs();
        }
    }

    // metodo per la determinazione del nome del file di log
    private String generaNome() {
        LocalDateTime momento = LocalDateTime.now();
        DateTimeFormatter formattatore = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String dataOra = momento.format(formattatore);

        // Formato nome file: <nome_classe>_<yyyy-MM-dd>_<HH-mm-ss>.log
        return CARTELLA_LOG + classe + "_" + dataOra + ".log";
    }

    public static String stampaMomento() {
        LocalDateTime momento = LocalDateTime.now();
        DateTimeFormatter formattatore = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dataOra = momento.format(formattatore);
        return dataOra;
    }

    // log generico, non classificato
    public void log(String messaggio) {
        String messaggioFinale = stampaMomento() + " " + messaggio;
        scrittore.println(messaggioFinale);
        scrittore.flush();
    }

    // log di errore
    public void logErrore(String messaggio) {
        messaggio = "[ERRORE]\t" + messaggio;
        log(messaggio);
    }

    // log di informazione
    public void logInfo(String messaggio) {
        messaggio = "[INFO]\t" + messaggio;
        log(messaggio);
    }

    // chiude il PrintWriter
    public void close() {
        if (scrittore != null)
            scrittore.close();
    }

    // ConsolePrintWriter è una estensione di PrintWriter che scrive i log su
    // console
    private static class ConsolePrintWriter extends PrintWriter {
        ConsolePrintWriter() {
            super(new OutputStreamWriter(System.out)); // reindirizza verso system.out
        }

        @Override
        public void close() {
        } // Metodo no-op perchè non c'è nulla da chiudere
    }
}

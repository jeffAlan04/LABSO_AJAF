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
    private String identificativo;

    public Logger(String classe, String identificativo) {
        this.classe = classe;
        this.identificativo = identificativo;

        PrintWriter scrittoreTemporaneo;
        try {
            FileWriter f = new FileWriter(new File(generaNome()), true);
            scrittoreTemporaneo = new PrintWriter(f);
        } catch (IOException e) {
            System.out.println("Errore nella creazione del file di log. I loge verranno stampati su console");
            scrittoreTemporaneo = new ConsolePrintWriter();
        }

        this.scrittore = scrittoreTemporaneo;
    }

    private String generaNome() {
        LocalDateTime momento = LocalDateTime.now();
        DateTimeFormatter formattatore = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String dataOra = momento.format(formattatore);

        // Formato: <nome_classe>_<identificativo>_<data>_<ora>.log
        return "log/" + classe + "_" + identificativo + "_" + dataOra + ".log";
    }

    public static String stampaMomento() {
        LocalDateTime momento = LocalDateTime.now();
        DateTimeFormatter formattatore = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dataOra = momento.format(formattatore);
        return dataOra;
    }

    public void log(String messaggio) {
        String messaggioFinale = stampaMomento() + " " + messaggio;
        scrittore.println(messaggioFinale);
        scrittore.flush();
    }

    public void logErrore(String messaggio) {
        messaggio = "[ERRORE]\t" + messaggio;
        log(messaggio);
    }

    public void logInfo(String messaggio) {
        messaggio = "[INFO]\t" + messaggio;
        log(messaggio);
    }

    public void close() {
        if (scrittore != null)
            scrittore.close();
    }

    // ConsolePrintWriter è una estensione di PrintWriter che scrive i log su
    // console
    private static class ConsolePrintWriter extends PrintWriter {
        ConsolePrintWriter() {
            super(new OutputStreamWriter(System.out));
        }

        @Override
        public void close() {
        } // Metodo no-op perchè non c'è nulla da chiudere
    }

    // da eliminare, solo per testing
    public static void main(String[] args) {
        Logger prova = new Logger("Logger", "1");
        prova.logInfo("Sto facendo una prova");
        prova.close();
    }
}

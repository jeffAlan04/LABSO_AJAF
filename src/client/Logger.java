import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.File;

public class Logger {
  private static PrintWriter scrittore;

  public Logger() {
    try {
      FileWriter f = new FileWriter(new File("log/" + stampaMomento() + ".txt"), true);
      scrittore = new PrintWriter(f);
    } catch (IOException e) {
      // Rilancia l'eccezione sul chiamante così da interrompere l'esecuzione
      throw new RuntimeException("Errore durante la creazione di un file di log", e);
    }
  }

  public static String stampaMomento() {
    LocalDateTime momento = LocalDateTime.now();
    DateTimeFormatter formattatore = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    String dataOra = momento.format(formattatore);
    return dataOra;
  }

  public static void log(String messaggio) {
    String messaggioFinale = stampaMomento() + " " + messaggio;
    scrittore.println(messaggioFinale);
    scrittore.flush();
  }

  public static void logErrore(String messaggio) {
    messaggio = "[ERRORE]\t" + messaggio;
    log(messaggio);
  }

  public static void logInfo(String messaggio) {
    messaggio = "[INFO]\t" + messaggio;
    log(messaggio);
  }

  public static void close() {
    if (scrittore != null)
      scrittore.close();
  }

  // da eliminare, solo per testing
  public static void main(String[] args) {
    Logger prova = new Logger();
    prova.logInfo("Sto facendo una prova");
    prova.close();
  }
}

import java.io.FileWriter;
import java.io.IOException;
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

    try {
      FileWriter f = new FileWriter(new File(generaNome()), true);
      scrittore = new PrintWriter(f);
    } catch (IOException e) {
      // Rilancia l'eccezione sul chiamante così da interrompere l'esecuzione
      throw new RuntimeException("Errore durante la creazione di un file di log", e);
    }
  }

  public String generaNome() {
    LocalDateTime momento = LocalDateTime.now();
    DateTimeFormatter formattatore = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    String dataOra = momento.format(formattatore);
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

  // da eliminare, solo per testing
  public static void main(String[] args) {
    Logger prova = new Logger("Logger", "1");
    prova.logInfo("Sto facendo una prova");
    prova.close();
  }
}

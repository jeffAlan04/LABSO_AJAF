import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
  private static PrintWriter scrittore;

  public Logger() {
    try {
      FileWriter f = new FileWriter(new File(generaNome()), true);
      scrittore = new PrintWriter(f);
    } catch (IOException e) {
      // Rilancia l'eccezione sul chiamante così da interrompere l'esecuzione
      throw new RuntimeException("Errore durante la creazione di un file di log", e);
    }
  }

  public static String generaNome() {
    LocalDateTime momento = LocalDateTime.now();
    DateTimeFormatter formattatore = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    String dataOra = momento.format(formattatore);
    return "log_" + dataOra + ".txt";
  }

  public static void log(String messaggio) {
    scrittore.println(messaggio);
    scrittore.flush();
  }

  public static void close() {
    if (scrittore != null)
      scrittore.close();
  }
}

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Logger {
  private static PrintWriter scrittore;

  public Logger() {
    // Creazione File di log
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

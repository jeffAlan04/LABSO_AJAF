import java.io.File;
import java.util.LinkedList;

public class GestioneRisorse {

  public static void aggiungiRisorsa(String nome, String contenuto) {

  }

  public static boolean risorsaPresente(String nome) {
    File directory = new File("/risorse");
    File[] risorse = directory.listFiles();

    if (risorse != null & risorse.length != 0) {
      for (File risorsa : risorse) {
        if (nome == risorsa.getName()) {
          return true;
        }
      }
    }

    return false;
  }
}

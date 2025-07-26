import java.io.File;
import java.io.PrintWriter;
import java.util.LinkedList;

public class GestioneRisorse {

  public static void aggiungiRisorsa(String nome, String contenuto) {

  }

  public static String risorsaPresente(String nome) {
    File directory = new File("risorse");
    File[] risorse = directory.listFiles();

    if (risorse != null && risorse.length != 0) {
      for (File risorsa : risorse) {
        if (nome.equals(risorsa.getName())) {
          return "true";
        }
      }
    }

    return "false";
  }
// Metodo per il comando listdata local
  public static void eseguiListDataLocal(){
    File input = new File("risorse");
    System.out.println("Path assoluto: " + input.getAbsolutePath());
    File[] risorse = input.listFiles();

    if (risorse == null || risorse.length == 0){
      System.out.println("Nessuna risorsa disponibile");
      return;
    }
    System.out.println("Risorse: ");
    for (File f : risorse) {
      if(f.isFile()){
        System.out.println("- " + f.getName());
      }
    }
  }
}

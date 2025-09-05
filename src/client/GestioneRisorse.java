import java.io.File;
import java.io.FileWriter;
import java.util.LinkedList;

public class GestioneRisorse {

    // Metodo per verificare la presenza di una risorsa all'interno della cartella
    // risorse
    public static String risorsaPresente(String nome) {
        controlloCartella();

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
    public static void eseguiListDataLocal() {
        controlloCartella();

        File input = new File("risorse");
        File[] risorse = input.listFiles();

        if (risorse == null || risorse.length == 0) {
            System.out.println("Nessuna risorsa disponibile");
            return;
        }
        System.out.println("Risorse: ");
        for (File f : risorse) {
            if (f.isFile()) {
                System.out.println("- " + f.getName());
            }
        }
    }

    // Metodo per il comando add <nome risorsa> <contenuto>
    public static void eseguiAdd(String nomeFile, String contenuto) {
        try {
            controlloCartella();

            File nuovoFile = new File(cartella, nomeFile);

            FileWriter writer = new FileWriter(nuovoFile);
            writer.write(contenuto);
            writer.close();

            System.out.println("File: " + nomeFile + " creato con successo");
        } catch (Exception e) {
            System.out.println("Errore nella creazione della risorsa");
        }
    }

    private static void controlloCartella() {
        File cartella = new File("risorse");
        if (!cartella.exists()) {
            cartella.mkdirs();
        }
    }
}

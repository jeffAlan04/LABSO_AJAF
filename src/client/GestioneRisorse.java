import java.io.File;
import java.io.FileWriter;
import java.util.LinkedList;

public class GestioneRisorse {

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
    public static void eseguiListDataLocal() {
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
            File cartella = new File("risorse/");
            if (!cartella.exists()) {
                cartella.mkdirs();
            }

            File nuovoFile = new File(cartella, nomeFile);

            FileWriter writer = new FileWriter(nuovoFile);
            writer.write(contenuto);
            writer.close();

            System.out.println("File: " + nomeFile + " creato con successo");
        } catch (Exception e) {
            System.out.println("Errore nella creazione della risorsa");
        }
    }

    // Metodo per il comando listdata_remote
    public static void eseguiListDataRemote(String risposta) {
        if (risposta == null || risposta.isEmpty()
                || risposta.trim().toLowerCase().contains("nessuna risorsa disponibile")) {
            System.out.println("Nessuna risorsa disponibile");
            return;
        }
        System.out.println("Risorse: ");
        String[] righe = risposta.split(";");

        for (String riga : righe) {

            // Divide la stringa in due parti: risosa (prima dei :) e peers (dopo :)
            String[] parti = riga.split(":", 2);

            String risorsa = parti[0].trim();
            String peers = parti.length > 1 ? parti[1].trim() : "";

            System.out.println(" - " + risorsa + ": " + peers);
        }

    }
}

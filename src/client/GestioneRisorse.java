import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class GestioneRisorse {
    private static final String CARTELLA_RISORSE = "risorse";

    // Metodo per verificare la presenza di una risorsa all'interno della cartella
    // risorse
    public static String risorsaPresente(String nome) {
        controlloCartella();

        File directory = new File(CARTELLA_RISORSE);

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

        File input = new File(CARTELLA_RISORSE);

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

    // Metodo per aggiungere una risorsa in locale
    public static boolean eseguiAdd(String nomeFile, String contenuto) {
        try {
            controlloCartella();
            File nuovoFile = new File(new File(CARTELLA_RISORSE), nomeFile);
            FileWriter writer = new FileWriter(nuovoFile);
            writer.write(contenuto);
            writer.close();
            return true;

        } catch (Exception e) {
            System.out.println("Errore nella creazione del file " + nomeFile + " in locale");
            return false;
        }
    }

    // Metodo per la stampa della tabella di master
    public static void eseguiListDataRemote(String risposta) {
        if (risposta == null || risposta.isEmpty()
                || risposta.trim().toLowerCase().contains("nessuna risorsa disponibile")) {
            System.out.println("Nessuna risorsa disponibile");
            return;
        }
        System.out.println("Risorse: ");
        String[] righe = risposta.split(";");

        for (String riga : righe) {
            System.out.println("- " + riga);
        }
    }

    // Metodo per il controllo della presenza della cartella /risorse
    private static void controlloCartella() {
        File cartella = new File(CARTELLA_RISORSE);
        if (!cartella.exists()) {
            cartella.mkdirs();
        }
    }
}

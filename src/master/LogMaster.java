import java.io.*;
import java.time.*;
import java.time.format.*;

public class LogMaster {

    // Nome cartella Salvatagio
    private final String DIR = ".log/";
    // Nome del file delle informazioni
    private final String FILE = DIR + "logMaster.log";
    // formattazione orario (formattato HH:mm)
    private final DateTimeFormatter FormattoOrario = DateTimeFormatter.ofPattern("HH:mm");

    // Crea la cartella dei log se non esiste
    public LogMaster() {
        File dir = new File(DIR);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    // Metodo per scrivere i download riuscito
    public void downloadSuccesso(String risorsa, String peerSorgente, String peerDestinazione) {
        scriviLog(risorsa, peerSorgente, peerDestinazione, true);
    }

    // Metodo per scrivere i download falliti
    public void downloadFallito(String risorsa, String peerSorgente, String peerDestinazione) {
        scriviLog(risorsa, peerSorgente, peerDestinazione, false);
    }

    // Scrive l'esito dell'operazione sul file
    private void scriviLog(String risorsa, String peerSorgente, String peerDestinazione, boolean esito) {
        String orario = LocalDateTime.now().format(FormattoOrario);
        String riga = orario + " " + risorsa +
                " da: " + peerSorgente +
                " a: " + peerDestinazione +
                (esito ? " Ok" : " Fallito");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE, true))) {
            bw.write(riga);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Errore scrittura log: " + e.getMessage());
        }
    }

    // Stampa tutto il contenuto del file di Log
    public void stampa() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            System.out.println("Risorse scaricati: ");
            String linea;
            // Legge riga per riga e stampa
            while ((linea = br.readLine()) != null) {
                System.out.println("- " + linea);
            }
        } catch (IOException e) {
            System.err.println("Errore lettura log: " + e.getMessage());
        }
    }
}

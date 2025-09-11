import java.io.*;
import java.time.*;
import java.time.format.*;

public class LogMaster {

    // Nome cartella Salvatagio
    private final String CARTELLA_LOG = ".log/";
    // Nome del file delle informazioni
    private final String FILE_LOG = CARTELLA_LOG + "logMaster.log";
    // formattazione orario (formattato HH:mm)
    private final DateTimeFormatter FormattoOrario = DateTimeFormatter.ofPattern("HH:mm");

    // Crea la cartella dei log se non esiste
    public LogMaster() {
        File dir = new File(CARTELLA_LOG);
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
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_LOG, true))) {
            bw.write(riga);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Errore scrittura log: " + e.getMessage());
        }
    }

    // Stampa tutto il contenuto del file di Log
    public void stampa() {
        File fileLog = new File(FILE_LOG);

        if (!fileLog.exists()) {
            System.out.println("Non ci sono log al momento");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_LOG))) {
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

package master;

import java.io.*;
import java.time.*;
import java.time.format.*;

public class Log {

    // Nome cartella Salvatagio
    private final String DIR = "LogOut";
    // Nome del file delle informazioni
    private final String FILE = DIR + "/log.txt";
    // formattazione orario (formattato HH:mm)
    private final DateTimeFormatter FormattoOrario = DateTimeFormatter.ofPattern("HH:mm");
    // unica istanza
    private static Log instance;

    private Log() {
        File dir = new File(DIR);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    public static synchronized Log getInstance() {
        if (instance == null) {
            instance = new Log();
        }
        return instance;
    }

    //Metodo per scrivere i download riuscito
    public synchronized  void downloadSuccesso(String risorsa, String peerSorgente, String peerDestinazione){
        scriviLog(risorsa, peerSorgente, peerDestinazione, true);
    }

    //Metodo per scrivere i download falliti
    public synchronized  void downloadFallito(String risorsa, String peerSorgente, String peerDestinazione){
        scriviLog(risorsa, peerSorgente, peerDestinazione, false);
    }

    // Scrive l'esito dell'operazione sul file
    private void scriviLog(String risorsa, String peerSorgente, String peerDestinazione, boolean esito) {
        String orario = LocalDateTime.now().format(FormattoOrario);
        String riga = orario + " " + risorsa +
                " da: " + peerSorgente +
                " a: " + peerDestinazione +
                (esito ? " Ok" : " Fallito");
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(FILE, true))){
            bw.write(riga);
            bw.newLine();
        }catch (IOException e){
            System.err.println("Errore scrittura log: " + e.getMessage());
        }
    }

    // Stampa tutto il contenuto del file di Log
    public synchronized  void stampa(){
        System.out.println("Risorse scaricati: ");
        try(BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                System.out.println("- " + linea);
            }
        } catch (IOException e) {
            System.err.println("Errore lettura log: " + e.getMessage());
        }
    }

    public static void main(String[] args){
        Log log = Log.getInstance();
        log.downloadSuccesso("R0", "peer0", "peer1");
        log.downloadFallito("R1", "peer1","peer0");
        log.downloadSuccesso("R2", "peer0", "peer1");

        log.stampa();

    }
}

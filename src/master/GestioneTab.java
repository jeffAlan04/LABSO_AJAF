import java.util.*;
import java.io.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

public class GestioneTab {
    private ObjectMapper mapper = new ObjectMapper();
    private Map<String, Set<String>> tabella = new HashMap<>();
    private Logger logger;

    private final String CARTELLA_RISORSE = "risorse_rete";
    private final String FILE_PATH = CARTELLA_RISORSE + "/tabella.json";

    // Inizializza il logger e carica i dati dal file
    public GestioneTab() {
        logger = new Logger("GestioneTab");
        caricaDaFile();
    }

    // Restituisce tutte le risorse con i peer associati
    public String getRisorse() {
        String risposta = "";

        for (String risorsa : tabella.keySet()) {
            risposta += risorsa + ": " + tabella.get(risorsa) + ";";
        }

        return risposta;
    }

    // Restituisce il primo peer disponibile di una specifica risorsa
    public String getPrimoPeer(String risorsa) {
        Set<String> peers = tabella.get(risorsa);
        if (peers == null || peers.isEmpty()) {
            return null;
        }
        return peers.iterator().next(); // Il primo valore
    }

    // Aggiunge una nuova risorsa alla tabella
    private void aggiungiRisorsa(String risorsa) {
        tabella.put(risorsa, new HashSet<String>());
    }

    // Aggiunge un nuovo peer con le sue risorse alla tabella
    public String aggiungiPeer(String indirizzoIp, Set<String> risorse) {
        Map<String, Set<String>> backup = backupTabella();
        for (String risorsa : risorse) {
            if (tabella.containsKey(risorsa)) { // La risorsa esiste
                tabella.get(risorsa).add(indirizzoIp);
            } else { // La risorsa non esiste
                aggiungiRisorsa(risorsa);
                tabella.get(risorsa).add(indirizzoIp);
            }
        }

        if (salvaSuFile()) {
            logger.logInfo("Informazioni peer " + indirizzoIp + " aggiunte con successo.");
            return "aggiunto";
        } else {
            tabella = backup; // Rollback in caso di errore
            logger.logErrore("Errore nel salvataggio dopo l'aggiunta delle informazioni peer " + indirizzoIp + ".");
            return "non_aggiunto";
        }
    }

    // Rimuove un peer in una specifica risorsa
    public void rimuoviPeerInRisorsa(String indirizzoIp, String risorsa) {
        Map<String, Set<String>> backup = backupTabella();
        if (tabella.get(risorsa).remove(indirizzoIp)) {
            tabella.entrySet().removeIf(entry -> entry.getValue().isEmpty()); // Rimuove la risorsa se non ha peer associati
            if (salvaSuFile()) {
                logger.logInfo("Rimozione " + indirizzoIp + " dalla risorsa " + risorsa + " avvenuta con successo.");
            } else {
                tabella = backup; // Rollback in caso di errore
                logger.logErrore("Errore nel salvataggio dopo la rimozione di " + indirizzoIp + " dalla risorsa " + risorsa + ".");
            }
        } else {
            logger.logErrore("Impossibile rimuovere " + indirizzoIp + " dalla risorsa " + risorsa + "... uno dei due non presente.");
        }
    }

    // Carica la tabella dal file JSON
    private void caricaDaFile() {
        File cartella = new File(CARTELLA_RISORSE);
        if (!cartella.exists()) {
            cartella.mkdirs();
        }

        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            logger.logInfo("File JSON vuoto o non trovato. Inizializzo tabella vuota.");
            tabella = new HashMap<>();
            return;
        }

        try {
            tabella = mapper.readValue(file, new TypeReference<Map<String, Set<String>>>() {});
            logger.logInfo("Tabella caricata con successo.");
        } catch (IOException e) {
            logger.logErrore("Errore nel caricamento da file della tabella.");
            tabella = new HashMap<>();
        }
    }

    // Salva la tabella su file JSON
    private boolean salvaSuFile() {
        try {
            mapper.writeValue(new File(FILE_PATH), tabella);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // Crea una copia della tabella per operazioni di rollback
    private Map<String, Set<String>> backupTabella() {
        Map<String, Set<String>> backup = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : tabella.entrySet()) {
            backup.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }
        return backup;
    }
}

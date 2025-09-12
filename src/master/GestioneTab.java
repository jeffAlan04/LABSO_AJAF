import java.util.*;
import java.io.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

public class GestioneTab {
    private ObjectMapper mapper = new ObjectMapper();
    private Map<String, Set<String>> tabella = new HashMap<>();
    private Logger logger;

    private final String FILE_PATH = "risorse/tabella.json";
    private final String CARTELLA_RISORSE = "risorse";

    public GestioneTab() {
        logger = new Logger("GestioneTab");
        caricaDaFile();
    }

    // GET
    public String getRisorse() { // restituisci tutte le risorse (comando `listdata remote`)
        String risposta = "";

        for (String risorsa : tabella.keySet()) {
            risposta += risorsa + ": " + tabella.get(risorsa) + ";";
        }

        return risposta;
    }

    public String getPrimoPeer(String risorsa) { // restituisci il primo peer che ha quella risorsa
        Set<String> peers = tabella.get(risorsa);
        if (peers == null || peers.isEmpty()) {
            return null;
        }
        return peers.iterator().next();
    }

    // AGGIUNTA
    private void aggiungiRisorsa(String risorsa) {
        tabella.put(risorsa, new HashSet<String>());
    }

    public String aggiungiPeer(String indirizzoIp, Set<String> risorse) {
        Map<String, Set<String>> backup = backupTabella();
        for (String risorsa : risorse) {
            if (tabella.containsKey(risorsa)) { // esiste la risorsa
                tabella.get(risorsa).add(indirizzoIp);
            } else { // non esiste la risorsa
                aggiungiRisorsa(risorsa);
                tabella.get(risorsa).add(indirizzoIp);
            }
        }

        if (salvaSuFile()) {
            logger.logInfo("Informazioni peer " + indirizzoIp + " aggiunte con successo.");
            return "aggiunto";
        } else {
            tabella = backup;
            logger.logErrore("Errore nel salvataggio dopo l'aggiunta delle informazioni peer " + indirizzoIp + ".");
            return "non_aggiunto";
        }
    }

    // RIMOZIONE
    public void rimuoviPeerInRisorsa(String indirizzoIp, String risorsa) {
        Map<String, Set<String>> backup = backupTabella();
        if (tabella.get(risorsa).remove(indirizzoIp)) {
            tabella.entrySet().removeIf(entry -> entry.getValue().isEmpty());
            if (salvaSuFile()) {
                logger.logInfo("Rimozione " + indirizzoIp + " dalla risorsa " + risorsa + " avvenuta con successo.");
            } else {
                tabella = backup;
                logger.logErrore("Errore nel salvataggio dopo la rimozione di " + indirizzoIp + " dalla risorsa "
                        + risorsa + ".");
            }
        } else {
            logger.logErrore("Impossibile rimuovere " + indirizzoIp + " dalla risorsa " + risorsa
                    + "... uno dei due non presente.");
        }
    }

    // SALVATAGGIO E CARICAMENTO DATI
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

    private boolean salvaSuFile() {
        try {
            mapper.writeValue(new File(FILE_PATH), tabella);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private Map<String, Set<String>> backupTabella() {
        Map<String, Set<String>> backup = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : tabella.entrySet()) {
            backup.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }
        return backup;
    }
}

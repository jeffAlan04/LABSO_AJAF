import java.util.*;
import java.io.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

public class GestioneTab {
    private final String FILE_PATH = "risorse/tabella.json";
    private ObjectMapper mapper = new ObjectMapper();
    private Map<String, Set<String>> tabella = new HashMap<>();

    public GestioneTab() {
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
        Map<String, Set<String>> backup = backupTabella(tabella);
        for (String risorsa : risorse) {
            if (tabella.containsKey(risorsa)) { // esiste la risorsa
                tabella.get(risorsa).add(indirizzoIp);
            }
            else { // non esiste la risorsa
                aggiungiRisorsa(risorsa);
                tabella.get(risorsa).add(indirizzoIp);
            }
        }

        if (salvaSuFile()) {
            return "Informazioni peer " + indirizzoIp + " aggiunte con successo.";
        } else {
            tabella = backup;
            return "Errore nel salvataggio dopo l'aggiunta delle informazioni peer " + indirizzoIp + ".";
        }
    }


    // RIMOZIONE
    public String rimuoviPeer(String indirizzoIp) {
        int count = 0;
        Map<String, Set<String>> backup = backupTabella(tabella);

        for (String risorsa : tabella.keySet()) {
            if (tabella.get(risorsa).remove(indirizzoIp)) {
                count++;
            }
        }

        if (count > 0) {
            tabella.entrySet().removeIf(entry -> entry.getValue().isEmpty());
            if (salvaSuFile()) {
                return "Rimozione peer " + indirizzoIp + " avvenuta con successo.";
            } else {
                tabella = backup;
                return "Errore nel salvataggio dopo la rimozione del peer " + indirizzoIp + ".";
            }
        } else {
            return "Impossibile rimuovere peer " + indirizzoIp + "... non presente in tabella.";
        }
    }

    public String rimuoviPeerInRisorsa(String indirizzoIp, String risorsa) {
        Map<String, Set<String>> backup = backupTabella(tabella);
        if (tabella.get(risorsa).remove(indirizzoIp)) {
            tabella.entrySet().removeIf(entry -> entry.getValue().isEmpty());
            if (salvaSuFile()) {
                return "Rimozione " + indirizzoIp + " dalla risorsa " + risorsa + " avvenuta con successo.";
            } else {
                tabella = backup;
                return "Errore nel salvataggio dopo la rimozione di " + indirizzoIp + " dalla risorsa " + risorsa + ".";
            }
        }
        else {
            return "Impossibile rimuovere " + indirizzoIp + " dalla risorsa " + risorsa + "... uno dei due non presente.";
        }
    }

    public String rimuoviRisorsa(String risorsa) {
        if (tabella.containsKey(risorsa)) {
            Map<String, Set<String>> backup = backupTabella(tabella);
            tabella.remove(risorsa);
            if (salvaSuFile()) {
                return "Rimozione risorsa " + risorsa + " avvenuta con successo.";
            }
            else {
                tabella = backup;
                return "Errore nel salvataggio dopo la rimozione della risorsa " + risorsa + ".";
            }
        }
        return "Impossibile rimuovere risorsa " + risorsa + "... non presente in tabella.";
    }


    // SALVATAGGIO E CARICAMENTO DATI
    private void caricaDaFile() {
        File file = new File(FILE_PATH);

        if (!file.exists() || file.length() == 0) {
            System.out.println("File JSON vuoto o non trovato. Inizializzo tabella vuota.");
            tabella = new HashMap<>();
            return;
        }

        try {
            tabella = mapper.readValue(file, new TypeReference<Map<String, Set<String>>>() {});
            System.out.println("Tabella caricata con successo.");
        }
        catch (IOException e) {
            System.out.println("Errore nel caricamento da file della tabella.");
            tabella = new HashMap<>();
        }
    }

    private boolean salvaSuFile() {
        try {
            mapper.writeValue(new File(FILE_PATH), tabella);
            return true;
        }
        catch (IOException e) {
            return false;
        }
    }

    private Map<String, Set<String>> backupTabella(Map<String, Set<String>> tabella) {
        Map<String, Set<String>> backup = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : tabella.entrySet()) {
            backup.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }
        return backup;
    }
}




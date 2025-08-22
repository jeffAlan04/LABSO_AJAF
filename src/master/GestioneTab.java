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
            risposta += risorsa + ": " + tabella.get(risorsa) + "\n";
        }

        return risposta;
    }

    public String getPeers(String risorsa) { // restituisci tutti peer che hanno quella risorsa
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
        for (String risorsa : risorse) {
            if (tabella.containsKey(risorsa)) { // esiste la risorsa
                tabella.get(risorsa).add(indirizzoIp);
            }
            else { // non esiste la risorsa
                aggiungiRisorsa(risorsa);
                tabella.get(risorsa).add(indirizzoIp);
            }
        }

        salvaSuFile();
        return "Informazioni peer " + indirizzoIp + " aggiunte con successo.";
    }


    // RIMOZIONE
    public String rimuoviPeer(String ipAddress) { // forse da rimuovere
        int count = 0;

        for (String risorsa : tabella.keySet()) {
            if (tabella.get(risorsa).remove(ipAddress)) {
                count++;
            }
        }

        if (count > 0) {
            tabella.entrySet().removeIf(entry -> entry.getValue().isEmpty());
            salvaSuFile();
            return "Rimozione peer " + ipAddress + " avvenuta con successo.";
        } else {
            return "Impossibile rimuovere peer " + ipAddress + "... non presente in tabella.";
        }
    }

    public String rimuoviRisorsa(String risorsa) {
        if (tabella.containsKey(risorsa)) {
            tabella.remove(risorsa);
            salvaSuFile();
            return "Rimozione risorsa " + risorsa + " avvenuta con successo.";
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
        }
        catch (Exception e) {
            System.out.println("Errore nel caricamento da file della tabella: " + e.getMessage());
            tabella = new HashMap<>();
        }
    }

    private void salvaSuFile() {
        try {
            mapper.writeValue(new File(FILE_PATH), tabella);
        }
        catch (Exception e) {
            System.out.println("Errore nel salvataggio su file della tabella.");
        }
    }
}




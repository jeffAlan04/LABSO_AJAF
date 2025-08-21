import java.util.*;
import java.io.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

public class GestioneTab {
    private final String FILE_PATH = "risorse/tabella.json";
    private ObjectMapper mapper = new ObjectMapper();
    private Map<String, List<String>> tabella = new HashMap<>();

    // GET
    public String getRisorse() { // restituisci tutte le risorse (comando `listdata remote`)
        String risposta = "";
        
        for (String risorsa : tabella.keySet()) {
            risposta += risorsa + ": " + tabella.get(risorsa) + "\n";
        }

        return risposta;
    }

    public List<String> getPeers(String risorsa) { // restituisci tutti peer che hanno quella risorsa
        if (tabella.containsKey(risorsa)) {
            return tabella.get(risorsa);
        }

        return new ArrayList<String>();
    }


    // AGGIUNTA
    private void aggiungiRisorsa(String risorsa) {
        tabella.put(risorsa, new ArrayList<String>());
    }

    public void aggiungiPeer(String indirizzoIp, List<String> risorse) {
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
    }


    // RIMOZIONE
    public String rimuoviPeer(String ipAddress) {
        int count = 0;

        for (String risorsa : tabella.keySet()) {
            if (tabella.get(risorsa).remove(ipAddress)) {
                count++;
            }
        }

        if (count > 0) {
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
        try {
            tabella = mapper.readValue(new File(FILE_PATH), new TypeReference<Map<String, List<String>>>() {});
        }
        catch (IOException ioException) {
            System.out.println("Caricamento da file fallito... tabella JSON non trovata.");
        }
        catch (Exception e) {
            System.out.println("Errore nel caricamento da file della tabella.");
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


    public static void main(String[] args) {
        // fare test
    }
}




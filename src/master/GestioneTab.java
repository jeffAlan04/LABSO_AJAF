import java.util.*;
import java.io.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

public class GestioneTab {
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
        if (tabella.keySet().contains(risorsa)) {
            return tabella.get(risorsa);
        }

        return new ArrayList<String>();
    }


    // AGGIUNTA
    public void aggiungiRisorsa(String risorsa) {
        tabella.put(risorsa, new ArrayList<String>());
    }

    public void aggingiPeer(String indirizzoIp, List<String> risorse) {
        for (String risorsa : risorse) {
            if (tabella.containsKey(risorsa)) { // esiste la risorsa
                tabella.get(risorsa).add(indirizzoIp);
            }
            else { // non esiste la risorsa
                aggiungiRisorsa(risorsa);
            }
        }
    }


    // RIMOZIONE
    public String rimuoviPeer(String ipAddress) {
        int count = 0;
        for (String risorsa : tabella.keySet()) {
            if (tabella.get(risorsa).contains(ipAddress)) {
                tabella.get(risorsa).remove(ipAddress);
                count = 1;
            }
        }

        if (count == 1) {
            return "Rimozione peer" + ipAddress + " avvenuta con successo.";
        }
        return "Impossibile rimuovere peer " + ipAddress + "... non presente in tabella.";
    }

    public String rimuoviRisorsa(String risorsa) {
        if (tabella.containsKey(risorsa)) {
            tabella.remove(risorsa);
            return "Rimozione risorsa " + risorsa + "avvenuta con successo.";
        }
        return "Impossibile rimuovere risorsa " + risorsa + "... non presente in tabella.";
    }


    // SALVATAGGIO E CARICAMENTO DATI
    public void caricaDaFile() {
        
    }

    public void salvaSuFile() {

    }


    public static void main(String[] args) {
        // fare test
    }
}




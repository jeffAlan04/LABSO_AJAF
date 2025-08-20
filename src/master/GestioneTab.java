import java.util.*;
import java.io.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

public class GestioneTab {
    private Map<String, List<String>> tabella = new HashMap<>();

    // GET
    public List<String> getRisorse() {
        // Restituisci tutte le risorse (comando `listdata remote`)
    }

    public List<String> getPeers(String risorsa) {
        // Restituisci tutti peer che hanno quella risorsa
    }


    // AGGIUNTA
    public void aggiungiRisorsa(String risorsa) {
        tabella.put(risorsa, new ArrayList<>());
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
        if (...) {
            return "Rimozione peer avvenuta con successo!";
        }
        else {
            return "Rimozione peer fallita.";
        }
    }

    public String rimuoviRisorsa(String risorsa) {
        if (...) {
            return "Rimozione risorsa avvenuta con successo!";
        }
        else {
            return "Rimozione risorsa fallita.";
        }
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




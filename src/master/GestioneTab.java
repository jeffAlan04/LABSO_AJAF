import java.util.*;

public class GestioneTab {
    private Map<String, List<String>> risorse = new HashMap<>();

    // GET
    public List<String> getRisorse() {
        // Restituisci tutte le risorse (comando `listdata remote`)
    }

    public List<String> getPeers(String risorsa) {
        // Restituisci tutti peer che hanno quella risorsa
    }


    // AGGIUNTA
    public void aggiungiRisorsa(String risorsa, List<String> indirizziIp) {
        // Controlla che la risorsa esista
        // se non esiste la crei
    }

    public void aggingiPeer(String indirizzoIp, List<String> risorse) {
        // Per ogni risorsa del peer da aggiungere chiamare metodo `aggiungiRisorsa()` 
        // che aggiunge questo peer alla lista di ogni risorsa che contiene
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




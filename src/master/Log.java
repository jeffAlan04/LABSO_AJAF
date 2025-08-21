package master;

import java.time.*;
import java.time.format.*;

public class Log {
    // Orario del download (formattato HH:mm)
    private final String orario;
    // Nome della risorsa scaricata
    private final String risorsa;
    // Peer sorgente da cui è stata scaricata la risorsa
    private final String peerSorgente;
    // Peer destinazione che ha ricevuto la risorsa
    private final String peerDestinazione;
    // Esito del download (true = successo, false = fallito)
    private final boolean esito;

    public Log(String Risorsa, String peerSorgente, String peerDestinazione, boolean esito){
        this.orario = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        this.risorsa = Risorsa;
        this.peerSorgente = peerSorgente;
        this.peerDestinazione = peerDestinazione;
        this.esito = esito;
    }

    // Ritorna l'orario del download
    public String getOrario(){
        return orario;
    }

    // Ritorna il nome della risorsa
    public String getRisorsa(){
        return risorsa;
    }

    // Ritorna il peer sorgente
    public String getPeerSorgente(){
        return peerSorgente;
    }

    // Ritorna il peer destinazione
    public String getPeerDestinazione(){
        return peerDestinazione;
    }

    // Rappresentazione testuale del log
    @Override
    public String toString() {
        return orario + " " + risorsa +
                " da: " + peerSorgente +
                " a: " + peerDestinazione +
                (esito ? " Ok" : " Fallito");
    }
}

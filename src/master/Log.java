package master;

import java.io.*;
import java.time.*;
import java.time.format.*;

public class Log {

    // Nome cartella Salvatagio
    private final String DIR = "LogOut";
    // Nome del file delle informazioni
    private final String FILE = "log.txt";
    // formattazione orario (formattato HH:mm)
    private final DateTimeFormatter FormattoOrario = DateTimeFormatter.ofPattern("HH:mm");
    // Orario del download
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
        this.orario = LocalDateTime.now().format(FormattoOrario);
        this.risorsa = Risorsa;
        this.peerSorgente = peerSorgente;
        this.peerDestinazione = peerDestinazione;
        this.esito = esito;
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

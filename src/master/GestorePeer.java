package master;

import java.io.*;
import java.net.*;
import java.util.*;

class GestorePeer implements Runnable {
    // Socket del peer connesso
    private final Socket socket;
    // Riferimento alla tabella delle risorse condivise
    private final TabellaRisorse tabella;

    // Costruttore che inizializza socket e tabella
    public GestorePeer(Socket socket, TabellaRisorse tabella){
        this.socket = socket;
        this.tabella = tabella;
    }

    @Override
    public void run(){
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            // Identificatore unico peer basato su IP:porta
            String idPeer = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
            // Registra nome peer univoco tipo peer0, peer1
            String nomePeer = MasterServer.registraPeer(idPeer);

            String messaggio;
            // Ciclo principale per gestire i messaggi del peer
            while ((messaggio = in.readLine()) != null) {
                if(messaggio.startsWith("AGGIUNGI")) {
                    // Comando "AGGIUNGI|R0" per aggiungere risorsa alla tabella
                    String risorsa = messaggio.split("\\|")[1];
                    tabella.aggiungiRisorsa(risorsa, idPeer);
                    out.println("OK: risorsa " + risorsa + " aggiunta");
                }
                else if(messaggio.startsWith("LISTA")) {
                    // Comando per ottenere tutte le risorse disponibili
                    out.println("risorse disponibili: " + tabella.ottieniTutteRisorse());
                }
                else if(messaggio.equals("QUIT")) {
                    // Comando per disconnettere il peer
                    out.println("Peer " + nomePeer + " disconesso");
                    break;
                }
                else if (messaggio.startsWith("DOWNLOAD")) {
                    // Comando "DOWNLOAD|R1" per scaricare una risorsa
                    String risorsa = messaggio.split("\\|")[1];
                    Set<String> indirizzi = tabella.ottieniPeerPerRisorsa(risorsa);

                    if (indirizzi.isEmpty()) {
                        // Nessun peer ha la risorsa richiesta
                        out.println("Nessun peer trovato per la risorsa " + risorsa);
                        MasterServer.aggiungiLog(new Log(risorsa, "-", nomePeer, false));
                    } else {
                        // Seleziona un peer che ha la risorsa
                        String peerDestinazioneId = indirizzi.iterator().next();
                        String nomePeerDestinazione = MasterServer.getNomePeer(peerDestinazioneId);

                        out.println("Scarica " + risorsa + " da " + nomePeerDestinazione);
                        // Aggiunge la riga di log del download
                        MasterServer.aggiungiLog(new Log(risorsa, nomePeerDestinazione, nomePeer, true));
                    }
                }
                else if (messaggio.equals("LOG")) {
                    out.println("Risorse scaricate:");
                    try (BufferedReader br = new BufferedReader(new FileReader("/home/james/Scrivania/LABSO_AJAF/src/master/log.txt"))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            out.println("- " + line);
                        }
                    } catch (IOException e) {
                        out.println("Errore lettura log da file: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Errore con peer: " + e.getMessage());
        }
    }
}

package master;

import java.io.*;
import java.net.*;
import java.util.*;

// Classe che gestisce la comunicazione con un singolo peer
class GestorePeer implements Runnable {
    private final Socket socket;
    private final TabellaRisorse tabella;

    public GestorePeer(Socket socket, TabellaRisorse tabella) {
        this.socket = socket;
        this.tabella = tabella;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String idPeer = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
            String nomePeer = Master.registraPeer(idPeer);
            tabella.registraPeer(nomePeer); // registra il peer nella tabella delle risorse

            String messaggio;
            while ((messaggio = in.readLine()) != null) {
                messaggio = messaggio.trim();

                if (messaggio.toUpperCase().startsWith("ADD ")) {
                    gestisciAdd(messaggio, nomePeer, out); // aggiungi risorsa
                } else if (messaggio.equalsIgnoreCase("LISTDATA REMOTE")) {
                    gestisciListData(out); // lista risorse remote
                } else if (messaggio.toUpperCase().startsWith("DOWNLOAD ")) {
                    gestisciDownload(messaggio, nomePeer, out); // richiedi download risorsa
                } else if (messaggio.equalsIgnoreCase("QUIT")) {
                    tabella.disconnettiPeer(nomePeer); // segnala disconnessione
                    out.println("Peer " + nomePeer + " disconnesso");
                    Master.notificaDisconnessione(socket.getRemoteSocketAddress().toString());
                    break;
                } else if (messaggio.equalsIgnoreCase("LOG")) {
                    gestisciLog(out); // mostra log delle risorse scaricate
                } else {
                    out.println("Comando non riconosciuto: " + messaggio);
                    out.println("Inserisci comando (ADD <nome> <contenuto>, LISTDATA REMOTE, DOWNLOAD <risorsa>, LOG, QUIT):");
                }
            }

        } catch (IOException e) {
            System.err.println("Errore con peer " + socket + ": " + e.getMessage());
        }
    }

    // Gestisce il comando ADD <nome> <contenuto>
    private void gestisciAdd(String messaggio, String nomePeer, PrintWriter out) {
        String[] parts = messaggio.split(" ", 3);
        if (parts.length < 3) {
            out.println("Errore: sintassi corretta -> ADD <nome> <contenuto>");
        } else {
            String risorsa = parts[1];
            tabella.aggiungiRisorsa(risorsa, nomePeer);
            out.println("OK: risorsa " + risorsa + " aggiunta da " + nomePeer);
            out.println("Inserisci comando (ADD <nome> <contenuto>, LISTDATA REMOTE, DOWNLOAD <risorsa>, LOG, QUIT):");
        }
    }

    // Gestisce il comando LISTDATA REMOTE
    private void gestisciListData(PrintWriter out) {
        Map<String, Set<String>> tutteOriginali = tabella.ottieniTutteRisorse();
        Map<String, Set<String>> tutte = new TreeMap<>();
        tutteOriginali.forEach((k, v) -> tutte.put(k, new HashSet<>(v)));

        out.println("Risorse:");
        out.println(String.format("%-15s %-20s", "Risorsa", "Peer"));
        out.println("-------------------------------------");

        tutte.forEach((risorsa, peerIds) -> {
            String nomiPeer = peerIds.isEmpty()
                    ? "non disponibile"
                    : peerIds.stream()
                    .map(Master::getNomePeer)
                    .sorted()
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("non disponibile");
            out.println(String.format("%-15s %-20s", risorsa, nomiPeer));
            out.println("Inserisci comando (ADD <nome> <contenuto>, LISTDATA REMOTE, DOWNLOAD <risorsa>, LOG, QUIT):");
        });
        out.println("END"); // segnala fine lista
    }

    // Gestisce il comando DOWNLOAD <risorsa>
    private void gestisciDownload(String messaggio, String nomePeer, PrintWriter out) {
        String risorsa = messaggio.split(" ", 2)[1];
        Set<String> indirizzi = new HashSet<>(tabella.ottieniPeerPerRisorsa(risorsa));
        indirizzi.removeIf(p -> !tabella.isAttivo(p));

        if (indirizzi.isEmpty()) {
            out.println("Nessun peer attivo trovato per la risorsa " + risorsa);
            out.println("Inserisci comando (ADD <nome> <contenuto>, LISTDATA REMOTE, DOWNLOAD <risorsa>, LOG, QUIT):");
            Master.aggiungiLog(new Log(risorsa, "-", nomePeer, false));
        } else {
            String peerDestinazioneId = indirizzi.iterator().next();
            String nomePeerDestinazione = Master.getNomePeer(peerDestinazioneId);
            out.println("Risorsa trovata: " + risorsa + " disponibile su " + nomePeerDestinazione);
            out.println("Inserisci comando (ADD <nome> <contenuto>, LISTDATA REMOTE, DOWNLOAD <risorsa>, LOG, QUIT):");
            Master.aggiungiLog(new Log(risorsa, nomePeerDestinazione, nomePeer, true));
        }
    }

    // Gestisce il comando LOG
    private void gestisciLog(PrintWriter out) {
        out.println("Risorse scaricate:");
        try (BufferedReader br = new BufferedReader(new FileReader("log.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                out.println("- " + line);
            }
            out.println("Inserisci comando (ADD <nome> <contenuto>, LISTDATA REMOTE, DOWNLOAD <risorsa>, LOG, QUIT):");
        } catch (IOException e) {
            out.println("Errore lettura log da file: " + e.getMessage());
        }
        out.println("END"); // fine lista log
    }
}

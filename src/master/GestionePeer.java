import java.util.*;
import java.io.*;
import java.net.*;

public class GestionePeer implements Runnable {
    private final Socket socket;
    private final Log logger;
    private final ArbitroLetturaScrittura arbitroTabella;
    private final ArbitroLetturaScrittura arbitroLog;
    private final GestioneTab gestioneTab;

    private final String COMANDO_LISTDATAREMOTE = "LISTDATA_REMOTE";
    private final String COMANDO_QUIT = "QUIT";
    private final String COMANDO_ADD = "ADD";
    private final String COMANDO_DOWNLOAD = "DOWNLOAD";

    public GestionePeer(Socket socket, Log logger, ArbitroLetturaScrittura arbitroLog, ArbitroLetturaScrittura arbitroTabella, GestioneTab gestioneTab) {
        this.socket = socket;
        this.logger = logger;
        this.arbitroLog = arbitroLog;
        this.arbitroTabella = arbitroTabella;
        this.gestioneTab = gestioneTab;
    }

    @Override
    public void run() {
        String indirizzoPeer = this.socket.getRemoteSocketAddress().toString();
        
        try (Scanner in = new Scanner(socket.getInputStream()); PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            // leggere lista risorse e mandarle a gestione tabella
            Set<String> risorsePeer = getRisorsePeer(in, out);
            out.println(salvataggioRisorsePeer(indirizzoPeer, risorsePeer));
            
            while (in.hasNextLine()) {
                String[] richiesta = in.nextLine().split(" ");
                String comando = richiesta[0];
                String nomeRisorsa = null;
                
                if (richiesta.length > 1) {
                    nomeRisorsa = richiesta[1];
                }

                switch (comando) {
                    case COMANDO_LISTDATAREMOTE:
                        out.println(listDataRemote());
                        break;

                    case COMANDO_QUIT:
                        return;

                    case COMANDO_ADD:
                        if (nomeRisorsa != null) {
                            out.println(addRisorsa(indirizzoPeer, Set.of(nomeRisorsa)));
                        }
                        else {
                            out.println("Specifica una risorsa da aggiungere.");
                        }
                        break;

                    case COMANDO_DOWNLOAD:
                        if (nomeRisorsa != null) {
                            downloadRisorsa(nomeRisorsa, indirizzoPeer, in, out);
                        }
                        else {
                            out.println("Specifica una risorsa da scaricare.");
                        }
                        break;

                    default:
                        out.println("Comando non riconosciuto.");
                        break;
                }
            }
        }
        catch (IOException e) {
            System.out.println("Errore con " + indirizzoPeer + " nell'apertura della socket.");
        }
        finally {
            try {
                this.arbitroTabella.inizioScrittura();
                if (!this.socket.isClosed()) {
                    this.socket.close();
                }
                System.out.println(this.gestioneTab.rimuoviPeer(indirizzoPeer));
            }
            catch (Exception e) {
                System.out.println("Errore con " + indirizzoPeer + " nella chiusura della socket.");
            }
            finally {
                this.arbitroTabella.fineScrittura();
            }
            System.out.println("Chiusura socket avvenuta con successo.");
        }
    }

    private Set<String> getRisorsePeer(Scanner in, PrintWriter out) {
        String[] scan = in.nextLine().split(" ");
        Set<String> risorsePeer = new HashSet<>();

        if ("REGISTRAZIONE_RISORSE".equals(scan[0])) {
            String risorsa;
            while (in.hasNextLine() && !"FINE".equals(risorsa = in.nextLine().trim())) {
                risorsePeer.add(risorsa);
            }
        }

        return risorsePeer;
    }

    private String salvataggioRisorsePeer(String indirizzoPeer, Set<String> risorsePeer) {
        try {
            this.arbitroTabella.inizioScrittura();
            return this.gestioneTab.aggiungiPeer(indirizzoPeer, risorsePeer);
        }
        finally {
            this.arbitroTabella.fineScrittura();
        }
    }
    
    private String listDataRemote() {
        try {
            this.arbitroTabella.inizioLettura();
            String risposta = this.gestioneTab.getRisorse();

            if (risposta == null || risposta.isEmpty()) {
                return "listdata remote: Nessuna risorsa disponibile.";
            }

            return risposta.trim();
        }
        finally {
            this.arbitroTabella.fineLettura();
        }
    }

    private String addRisorsa(String indirizzoPeer, Set<String> risorse) {
        try {
            this.arbitroTabella.inizioScrittura();
            return this.gestioneTab.aggiungiPeer(indirizzoPeer, risorse).trim();
        }
        finally {
            this.arbitroTabella.fineScrittura();
        }
    }

    private void downloadRisorsa(String risorsa, String peerSorgente, Scanner in, PrintWriter out) {
        while (true) {
            String peerDestinazione = getPeer(risorsa);

            if (peerDestinazione == null) {
                out.println("Non disponibile");
                scritturaLog(risorsa, peerSorgente, peerDestinazione, false);
                return;
            }

            out.println("Disponibile: " + peerDestinazione); // primo peer disponibile

            if (in.hasNextLine()) { // feedback
                String risposta = in.nextLine().trim();

                if ("true".equals(risposta)) {
                    scritturaLog(risorsa, peerSorgente, peerDestinazione, true);
                    out.println("Completato");
                    return;
                }
                else if ("false".equals(risposta)) {
                    scritturaLog(risorsa, peerSorgente, peerDestinazione, false);
                    rimuoviPeer(peerDestinazione, risorsa);
                }
                else {
                    scritturaLog(risorsa, peerSorgente, peerDestinazione, false);
                    out.println("Fallito");
                    return;
                }
            }
        }
    }

    private void scritturaLog(String risorsa, String peerSorgente, String peerDestinazione, boolean esito) {
        this.arbitroLog.inizioScrittura();
        if (esito) {
            this.logger.downloadSuccesso(risorsa, peerSorgente, peerDestinazione);
        }
        else {
            this.logger.downloadFallito(risorsa, peerSorgente, peerDestinazione);
        }
        this.arbitroLog.fineScrittura();
    }

    private String getPeer(String risorsa) {
        try {
            this.arbitroTabella.inizioLettura();
            return this.gestioneTab.getPeers(risorsa);
        }
        finally {
            this.arbitroTabella.fineLettura();
        }
    }

    private String rimuoviPeer(String indirizzoPeer, String risorsa) {
        try {
            this.arbitroTabella.inizioScrittura();
            return this.gestioneTab.rimuoviPeerInRisorsa(indirizzoPeer, risorsa);
        }
        finally {
            this.arbitroTabella.fineScrittura();
        }
    }
}

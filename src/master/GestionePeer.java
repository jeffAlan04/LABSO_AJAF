import java.util.*;
import java.io.*;
import java.net.*;

public class GestionePeer implements Runnable {
    private final Socket socket;
    private final ArbitroLetturaScrittura arbitroTabella;
    private final ArbitroLetturaScrittura arbitroLog;
    private final GestioneTab gestioneTab;

    private final String COMANDO_LISTDATAREMOTE = "LISTDATA_REMOTE";
    private final String COMANDO_QUIT = "QUIT";
    private final String COMANDO_ADD = "ADD";
    private final String COMANDO_DOWNLOAD = "DOWNLOAD";

    public GestionePeer(Socket socket, ArbitroLetturaScrittura arbitroLog, ArbitroLetturaScrittura arbitroTabella, GestioneTab gestioneTab) {
        this.socket = socket;
        this.arbitroLog = arbitroLog;
        this.arbitroTabella = arbitroTabella;
        this.gestioneTab = gestioneTab;
    }

    @Override
    public void run() {
        String indirizzoIpPeer = this.socket.getInetAddress().getHostAddress();
        
        try (Scanner in = new Scanner(socket.getInputStream()); PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            // leggere lista risorse e mandarle a gestione tabella
            Set<String> risorsePeer = getRisorsePeer(in, out);
            out.println(salvataggioRisorsePeer(indirizzoIpPeer, risorsePeer));
            
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
                            out.println(addRisorsa(indirizzoIpPeer, Set.of(nomeRisorsa)));
                        }
                        else {
                            out.println("Specifica una risorsa da aggiungere.");
                        }
                        break;

                    case COMANDO_DOWNLOAD:
                        if (nomeRisorsa != null) {
                            downloadRisorsa(nomeRisorsa, in, out);
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
            // ...
        }
        finally {
            try {
                this.arbitroTabella.inizioScrittura();
                if (!this.socket.isClosed()) {
                    this.socket.close();
                }
                System.out.println(this.gestioneTab.rimuoviPeer(indirizzoIpPeer));
            }
            catch (Exception e) {
                System.out.println("Errore chiusura socket: " + e.getMessage());
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

        if (scan[0].equals("REGISTRAZIONE_RISORSE")) {
            String risorsa;
            while (in.hasNextLine() && !"FINE".equals(risorsa = in.nextLine().trim())) {
                risorsePeer.add(risorsa);
            }
        }

        return risorsePeer;
    }

    private String salvataggioRisorsePeer(String indirizzoIpPeer, Set<String> risorsePeer) {
        try {
            this.arbitroTabella.inizioScrittura();
            return this.gestioneTab.aggiungiPeer(indirizzoIpPeer, risorsePeer);
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

    private String addRisorsa(String indirizzoIpPeer, Set<String> risorse) {
        try {
            this.arbitroTabella.inizioScrittura();
            return this.gestioneTab.aggiungiPeer(indirizzoIpPeer, risorse).trim();
        }
        finally {
            this.arbitroTabella.fineScrittura();
        }
    }

    private void downloadRisorsa(String risorsa, Scanner in, PrintWriter out) {
        while (true) {
            String peer = getPeer(risorsa);

            if (peer == null) {
                out.println("Non disponibile");
                // logNessunPeer
                return;
            }

            out.println("Disponibile: " + peer); // primo peer disponibile

            if (in.hasNextLine()) { // feedback
                String risposta = in.nextLine().trim();

                if (risposta.equals("true")) {
                    // logSuccesso
                    out.println("Completato");
                    return;
                }
                else if (risposta.equals("false")) {
                    // logFallimento
                    rimuoviPeer(peer, risorsa);
                }
                else {
                    // logFallimento
                    out.println("Fallito");
                    return;
                }
            }
        }
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

    private String rimuoviPeer(String indirizzoIpPeer, String risorsa) {
        try {
            this.arbitroTabella.inizioScrittura();
            return this.gestioneTab.rimuoviPeerInRisorsa(indirizzoIpPeer, risorsa);
        }
        finally {
            this.arbitroTabella.fineScrittura();
        }
    }
}

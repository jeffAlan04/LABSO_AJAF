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
        // comandi da gestire: listdata remote, quit, add risorsa, download risorsa
        try (Scanner in = new Scanner(socket.getInputStream()); PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            // leggere lista risorse e mandarle a gestione tabella
            String indirizzoIpPeer = this.socket.getInetAddress().getHostAddress();
            Set<String> risorsePeer = getRisorsePeer();
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
                        return;
                }
            }
        }
        catch (IOException e) {
            // ...
        }
    }

    private Set<String> getRisorsePeer() {
        // ...
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
                out.println("NESSUN PEER DISPONIBILE");
                // logNessunPeer
                return;
            }

            out.println("PEER DISPONIBILE: " + peer); // primo peer disponibile

            if (in.hasNextLine()) { // feedback
                String risposta = in.nextLine().trim();

                if (risposta.equals("true")) {
                    // logSuccesso
                    out.println("DOWNLOAD COMPLETATO");
                    return;
                }
                else if (risposta.equals("false")) {
                    // logFallimento
                    rimuoviPeer(peer, risorsa);
                }
                else {
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

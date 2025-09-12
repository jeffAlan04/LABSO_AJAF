import java.util.*;
import java.io.*;
import java.net.*;

public class GestionePeer implements Runnable {
    private Socket socket;
    private LogMaster loggerDownload;
    private ArbitroLetturaScrittura arbitroTabella;
    private ArbitroLetturaScrittura arbitroLog;
    private GestioneTab gestioneTab;
    private Logger logger;
    private String indirizzoPeer;

    private final String COMANDO_LISTDATAREMOTE = "LISTDATA_REMOTE";
    private final String COMANDO_QUIT = "QUIT";
    private final String COMANDO_ADD = "ADD";
    private final String COMANDO_DOWNLOAD = "DOWNLOAD";

    public GestionePeer(Socket socket, LogMaster loggerDownload, ArbitroLetturaScrittura arbitroLog,
            ArbitroLetturaScrittura arbitroTabella, GestioneTab gestioneTab) {
        this.socket = socket;
        this.loggerDownload = loggerDownload;
        this.arbitroLog = arbitroLog;
        this.arbitroTabella = arbitroTabella;
        this.gestioneTab = gestioneTab;
        logger = new Logger("GestionePeer");
    }

    @Override
    public void run() {
        indirizzoPeer = this.socket.getRemoteSocketAddress().toString();

        try (Scanner in = new Scanner(this.socket.getInputStream()); PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true)) {
            Set<String> risorsePeer = getRisorsePeer(in, out);
            String risposta = addRisorsa(risorsePeer);
            out.println(risposta);
            if ("aggiunto".equals(risposta)) {
                logger.logInfo("Informazioni peer " + indirizzoPeer + " aggiunte con successo.");
            }
            else {
                logger.logErrore("Errore aggiunta informazioni peer " + indirizzoPeer + ".");
                quit();
            }

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
                        logger.logInfo("Stampato elenco risorse disponibili.");
                        break;

                    case COMANDO_QUIT:
                        return;

                    case COMANDO_ADD:
                        if (nomeRisorsa != null) {
                            out.println(addRisorsa(Set.of(nomeRisorsa)));
                            logger.logInfo("Risorsa aggiunta.");
                        } else {
                            out.println("non_aggiunto");
                            logger.logErrore("Risorsa non specificata.");
                        }
                        break;

                    case COMANDO_DOWNLOAD:
                        if (nomeRisorsa != null) {
                            downloadRisorsa(nomeRisorsa, in, out);
                        } else {
                            out.println("Specifica una risorsa da scaricare.");
                            logger.logErrore("Risorsa non specificata.");
                        }
                        break;

                    default:
                        out.println("Comando non riconosciuto.");
                        logger.logErrore("Comando non riconosciuto.");
                        break;
                }
            }
        } catch (IOException e) {
            logger.logErrore("Errore con " + indirizzoPeer + " nell'apertura della socket.");
        } finally {
            quit();
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

    private String listDataRemote() {
        this.arbitroTabella.inizioLettura();
        String risposta = this.gestioneTab.getRisorse();
        this.arbitroTabella.fineLettura();

        if (risposta == null || risposta.isEmpty()) {
            logger.logErrore("listdata remote: nessuna risorsa disponibile.");
            return "non_disponibile";
        }

        return risposta.trim();
    }

    private String addRisorsa(Set<String> risorse) {
        this.arbitroTabella.inizioScrittura();
        String risposta = this.gestioneTab.aggiungiPeer(indirizzoPeer, risorse);
        this.arbitroTabella.fineScrittura();
        return risposta;
    }

    private void downloadRisorsa(String risorsa, Scanner in, PrintWriter out) {
        while (true) {
            String peerDestinazione = getPeer(risorsa);

            if (peerDestinazione == null) {
                out.println("non_disponibile");
                logger.logErrore("Peer non disponibile.");
                return;
            }

            out.println(peerDestinazione); // primo peer disponibile

            if (in.hasNextLine()) { // feedback
                String risposta = in.nextLine().trim();

                if ("true".equals(risposta)) {
                    scritturaLog(risorsa, peerDestinazione, true);
                    logger.logInfo("Download effettuato con successo da: " + peerDestinazione);
                    addRisorsa(new HashSet<>(Set.of(risorsa)));
                    return;
                } else {
                    scritturaLog(risorsa, peerDestinazione, false);
                    logger.logErrore("Impossibile effettuare download da: " + peerDestinazione);
                    rimuoviPeer(peerDestinazione, risorsa);
                }
            }
        }
    }

    private void scritturaLog(String risorsa, String peerDestinazione, boolean esito) {
        this.arbitroLog.inizioScrittura();
        if (esito) {
            this.loggerDownload.downloadSuccesso(risorsa, indirizzoPeer, peerDestinazione);
        } else {
            this.loggerDownload.downloadFallito(risorsa, indirizzoPeer, peerDestinazione);
        }
        this.arbitroLog.fineScrittura();
    }

    private String getPeer(String risorsa) {
        this.arbitroTabella.inizioLettura();
        String risposta = this.gestioneTab.getPrimoPeer(risorsa);
        this.arbitroTabella.fineLettura();
        return risposta;
    }

    private void rimuoviPeer(String indirizzoPeerDaRimuovere, String risorsa) {
        this.arbitroTabella.inizioScrittura();
        this.gestioneTab.rimuoviPeerInRisorsa(indirizzoPeerDaRimuovere, risorsa);
        this.arbitroTabella.fineScrittura();
    }

    public void quit() {
        try {
            if (!this.socket.isClosed()) {
                this.socket.close();
            }
            logger.logInfo("Chiusura socket di " + indirizzoPeer + " avvenuta con successo.");            
        } catch (IOException e) {
            logger.logErrore("Errore con la chiusura della socket di " + indirizzoPeer + ".");
        }
    }
}

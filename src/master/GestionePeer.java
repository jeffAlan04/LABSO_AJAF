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
    private String indirizzoPeerServer;
    private boolean terminato = false;

    private final String COMANDO_LISTDATAREMOTE = "LISTDATA_REMOTE";
    private final String COMANDO_QUIT = "QUIT";
    private final String COMANDO_ADD = "ADD";
    private final String COMANDO_DOWNLOAD = "DOWNLOAD";

    // Inizializza tutti i componenti per gestire un peer
    public GestionePeer(Socket socket, LogMaster loggerDownload, ArbitroLetturaScrittura arbitroLog,
            ArbitroLetturaScrittura arbitroTabella, GestioneTab gestioneTab) {
        this.socket = socket;
        this.loggerDownload = loggerDownload;
        this.arbitroLog = arbitroLog;
        this.arbitroTabella = arbitroTabella;
        this.gestioneTab = gestioneTab;
        logger = new Logger("GestionePeer");
    }

    // Gestisce la comunicazione con il peer
    @Override
    public void run() {
        indirizzoPeer = assegnaIP();

        // Try-with-resources per gestire automaticamente la chiusura di Scanner e PrintWriter
        try (Scanner in = new Scanner(this.socket.getInputStream());
                PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true)) {

            // Termina se non riesce a ottenere la porta del server del peer
            if (!assegnaIPserver(in, out)) {
                return;
            }

            Set<String> risorsePeer = getRisorsePeer(in, out);
            String risposta = addRisorsa(risorsePeer);
            out.println(risposta);
            if ("aggiunto".equals(risposta)) {
                logger.logInfo("Informazioni peer " + indirizzoPeer + " aggiunte con successo.");
            } else {
                logger.logErrore("Errore aggiunta informazioni peer " + indirizzoPeer + ".");
                return;
            }

            // Loop principale per gestire i comandi del peer
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
                        quitGestionePeer();
                        break;

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
                            out.println("non_disponibile");
                            logger.logErrore("Risorsa non specificata.");
                        }
                        break;

                    default:
                        out.println("Comando non riconosciuto.");
                        logger.logErrore("Comando non riconosciuto.");
                        break;
                }
            }
        } catch (NoSuchElementException e) {
            logger.logErrore("Chiusura inaspettata della connesione con " + indirizzoPeer + ".");
        } catch (IOException e) {
            logger.logErrore("Errore nella connessione con " + indirizzoPeer + ".");
        } finally {
            if (!terminato) {
                logger.logErrore("Thread " + indirizzoPeer + " terminato.");
                quitGestionePeer();
            }
        }
    }

    // Riceve l'elenco delle risorse che possiede il peer
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

    // Restituisce l'elenco di tutte le risorse disponibili nella rete
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

    // Aggiunge le risorse del peer nella tabella
    private String addRisorsa(Set<String> risorse) {
        this.arbitroTabella.inizioScrittura();
        String risposta = this.gestioneTab.aggiungiPeer(indirizzoPeerServer, risorse);
        this.arbitroTabella.fineScrittura();
        return risposta;
    }

    // Gestisce la richista di download di una risorsa
    private void downloadRisorsa(String risorsa, Scanner in, PrintWriter out) {
        while (true) {
            String peerDestinazione = getPeer(risorsa);

            if (peerDestinazione == null) { // Nessun peer disponibile
                out.println("non_disponibile");
                logger.logErrore("Peer non disponibile.");
                return;
            }

            out.println(peerDestinazione); // Primo peer disponibile

            if (in.hasNextLine()) { // Feedback dal peer
                String risposta = in.nextLine().trim();

                if ("true".equals(risposta)) {
                    scritturaLog(risorsa, peerDestinazione, true);
                    logger.logInfo("Download effettuato con successo da: " + peerDestinazione);
                    addRisorsa(new HashSet<>(Set.of(risorsa))); // Aggiunge la risorsa al peer richiedente
                    return;
                } else {
                    scritturaLog(risorsa, peerDestinazione, false);
                    logger.logErrore("Impossibile effettuare download da: " + peerDestinazione);
                    rimuoviPeer(peerDestinazione, risorsa); // Rimuove peer non funzionante
                }
            }
        }
    }

    // Registra l'esito di un tentativo di download nel log
    private void scritturaLog(String risorsa, String peerDestinazione, boolean esito) {
        this.arbitroLog.inizioScrittura();
        if (esito) {
            this.loggerDownload.downloadSuccesso(risorsa, indirizzoPeer, peerDestinazione);
        } else {
            this.loggerDownload.downloadFallito(risorsa, indirizzoPeer, peerDestinazione);
        }
        this.arbitroLog.fineScrittura();
    }

    // Ottiene il primo peer disponibile
    private String getPeer(String risorsa) {
        this.arbitroTabella.inizioLettura();
        String risposta = this.gestioneTab.getPrimoPeer(risorsa);
        this.arbitroTabella.fineLettura();
        return risposta;
    }

    // Rimuove un peer dalla tabella per una specifica risorsa
    private void rimuoviPeer(String indirizzoPeerDaRimuovere, String risorsa) {
        this.arbitroTabella.inizioScrittura();
        this.gestioneTab.rimuoviPeerInRisorsa(indirizzoPeerDaRimuovere, risorsa);
        this.arbitroTabella.fineScrittura();
    }

    // Estrae IP del peer dalla socket
    private String assegnaIP() {
        String ip = this.socket.getRemoteSocketAddress().toString();

        // Sostituisce localhost con indirizzo ip privato
        if ("/127.0.0.1".equals(ip.split(":")[0])) {
            try {
                String porta = ip.split(":")[1];
                ip = InetAddress.getLocalHost().toString();
                ip = ip + ":" + porta; // Mantiene la porta
            } catch (UnknownHostException e) {
                // Mantiene localhost
            }
        }

        // Rimozione dello / davanti all'indirizzo
        String[] parti = ip.split("/");
        if (parti.length > 1) {
            ip = parti[1];
        }

        return ip;
    }

    // Riceve e valida la porta del server del peer
    private boolean assegnaIPserver(Scanner in, PrintWriter out) {
        String[] scan = in.nextLine().trim().split(":");

        if (scan.length > 1 && "PORTA".equals(scan[0]) && !"0".equals(scan[1])) {
            this.indirizzoPeerServer = indirizzoPeer.split(":")[0] + ":" + scan[1];
            out.println("porta_ricevuta");
            logger.logInfo("Indirizzo PeerServer " + indirizzoPeerServer + " acquisito");
            return true;
        } else {
            out.println("porta_non_ricevuta");
            logger.logErrore("Errore nella ricezione della porta del PeerServer del peer " + indirizzoPeer);
            return false;
        }
    }

    // Chiude la connessione e rimuove il peer dalla lista
    private void quitGestionePeer() {
        terminato = true;

        synchronized (Master.listaGestoriPeer) {
            Master.listaGestoriPeer.remove(this);
        }
        
        quit();
    }

    public void quitMaster() {
        if (terminato) {
            return; // Evita doppio quit
        }
        terminato = true;
        quit();
    }

    private void quit() {
        try {
            if (!this.socket.isClosed()) {
                this.socket.close();
                logger.logInfo("Chiusura socket di " + indirizzoPeer + " avvenuta con successo.");
            }
            // Socket già chiusa
        } catch (IOException e) {
            logger.logErrore("Errore con la chiusura della socket di " + indirizzoPeer + ".");
        }
    }
}

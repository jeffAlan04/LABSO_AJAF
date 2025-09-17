import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class Client {
    private static PeerServer server;
    private static CountDownLatch latch;

    private final static String COMANDO_LISTDATA = "LISTDATA";
    private final static String COMANDO_LISTDATAREMOTE = "LISTDATA_REMOTE";
    private final static String COMANDO_QUIT = "QUIT";
    private final static String COMANDO_ADD = "ADD";
    private final static String COMANDO_DOWNLOAD = "DOWNLOAD";

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Utilizzo: java Client [indirizzo master] [porta]");
            return;
        }
        String indirizzoMaster = args[0];
        int porta = Integer.parseInt(args[1]);

        try (
                Socket s = new Socket(indirizzoMaster, porta);
                Scanner inputMaster = new Scanner(s.getInputStream());
                PrintWriter outputMaster = new PrintWriter(s.getOutputStream());
                Scanner tastiera = new Scanner(System.in);) {

            System.out.println("Connessione al master: " + s.getRemoteSocketAddress());

            avvioServer(); // Avvia PeerServer in contemporanea

            if (!trasmettiPorta(inputMaster, outputMaster) || !registrazioneRisorseLocali(inputMaster, outputMaster)) {
                server.terminaServer();
                return;
            }

            // Ciclo principale: attende comandi da console
            while (true) {
                System.out.print("> ");
                String messaggio = tastiera.nextLine().trim();

                // Switch sul primo token del messaggio
                switch (messaggio.split(" ")[0].toUpperCase()) {
                    case COMANDO_QUIT:
                        gestisciQuit(outputMaster);
                        return;

                    case COMANDO_LISTDATA:
                        tipoListData(messaggio, inputMaster, outputMaster);
                        break;

                    case COMANDO_DOWNLOAD:
                        gestisciDownload(messaggio, inputMaster, outputMaster);
                        break;

                    case COMANDO_ADD:
                        gestisciAdd(messaggio, outputMaster, inputMaster);
                        break;

                    default:
                        System.out.println("Comando non riconosciuto: " + messaggio);
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("Errore di connessione al master.");
        } catch (NoSuchElementException e) {
            System.out.println("Master disconnesso.");
        } finally {
            if (server != null) {
                server.terminaServer();
            }
        }
    }

    private static boolean trasmettiPorta(Scanner inputMaster, PrintWriter outputMaster) {

        int portaPeerServer;
        try {
            latch.await();
            portaPeerServer = server.getPorta();
        } catch (InterruptedException e) {
            portaPeerServer = 0;
        }

        outputMaster.println("PORTA:" + portaPeerServer);
        outputMaster.flush();
        String risposta = inputMaster.nextLine();

        if ("porta_ricevuta".equals(risposta)) {
            return true;
        }

        System.out.println("Errore nella trasmissione della porta di PeerServer");
        return false;
    }

    // Gestore della registraizone delle risorse locali
    private static boolean registrazioneRisorseLocali(Scanner inputMaster, PrintWriter outputMaster) {
        List<String> risorseLocali = new ArrayList<>();
        risorseLocali.add("REGISTRAZIONE_RISORSE");
        risorseLocali.addAll(GestioneRisorse.getRisorseLocali());
        risorseLocali.add("FINE");

        // Invio della lista delle risorse
        for (String riga : risorseLocali) {
            outputMaster.println(riga);
            outputMaster.flush();
        }

        // Ricezione del risultato dell'operazione su master
        String risposta = inputMaster.nextLine();
        if ("aggiunto".equals(risposta)) {
            return true;
        } else {
            System.out.println("Errore nella trasmissione delle risorse locali");
            return false;
        }
    }

    // Gestore di "listdata local" e "listdata remote"
    private static void tipoListData(String messaggio, Scanner inputMaster, PrintWriter outputMaster) {
        String[] partiMessaggio = messaggio.split(" ");
        if (partiMessaggio.length > 1) {
            String tipo = partiMessaggio[1].toUpperCase();

            if ("REMOTE".equals(tipo)) {
                gestisciListDataRemote(inputMaster, outputMaster);
            } else if ("LOCAL".equals(tipo)) {
                gestisciListDataLocal();
            } else {
                System.out.println("Comando non riconosciuto " + messaggio);
            }
        } else {
            System.out.println("Comando non riconosciuto " + messaggio);
        }
    }

    // Gestore del comando listdata_local
    private static void gestisciListDataLocal() {
        ArrayList<String> risorseLocali = GestioneRisorse.getRisorseLocali();

        if (risorseLocali.isEmpty()) {
            System.out.println("Nessuna risorsa presente");
            return;
        }

        System.out.println("Risorse: ");
        for (String risorsa : risorseLocali) {
            System.out.println("- " + risorsa);
        }
    }

    // Gestore del comando listdata_remote
    private static void gestisciListDataRemote(Scanner inputMaster, PrintWriter outputMaster) {
        outputMaster.println(COMANDO_LISTDATAREMOTE);
        outputMaster.flush();

        String risposta = inputMaster.nextLine();
        GestioneRisorse.eseguiListDataRemote(risposta);
    }

    // Gestore del comando download
    private static void gestisciDownload(String messaggio, Scanner inputMaster, PrintWriter outputMaster) {

        String[] parti = messaggio.split("\\s+");
        if (parti.length != 2) {
            System.out.println("Uso corretto: download <nome_risorsa>");
            return;
        }
        String nomeRisorsa = parti[1];

        // Se la risorsa è già presente in locale non viene scaricata
        if ("true".equals(GestioneRisorse.risorsaPresente(nomeRisorsa))) {
            System.out.println("Risorsa presente in locale");
            return;
        }

        outputMaster.println(COMANDO_DOWNLOAD + " " + nomeRisorsa);
        outputMaster.flush();

        // Legge la risposta del master che contiene l'indirizzo dell'host peer che
        // possiede la risorsa indicata.
        String risposta = inputMaster.nextLine();
        while (!"non_disponibile".equals(risposta)) {
            String indirizzoHostPeer = risposta.split(":")[0];
            int portaHostPeer = Integer.parseInt(risposta.split(":")[1]);

            PeerClient pc = new PeerClient(indirizzoHostPeer, portaHostPeer, nomeRisorsa);
            if (pc.avviaConnessione()) {
                outputMaster.println("true");
                outputMaster.flush();
                break;
            }

            outputMaster.println("false");
            outputMaster.flush();
            indirizzoHostPeer = inputMaster.nextLine();
        }
        if ("non_disponibile".equals(risposta)) {
            System.out.println("Download fallito: nessun peer disponibile");
        } else {
            System.out.println("Download avvenuto con successo");
        }
    }

    // Gestore del comando add
    private static void gestisciAdd(String messaggio, PrintWriter outputMaster, Scanner inputMaster) {
        String[] parti = messaggio.split("\\s+", 3);
        if (parti.length < 3) {
            System.out.println("Uso corretto: add nome_risorsa contenuto");
            return;
        }
        String nomeFile = parti[1];
        String contenuto = parti[2];

        if (!GestioneRisorse.eseguiAdd(nomeFile, contenuto)) {
            return; // in caso di errore nella creazione in locale interrompe il metodo
        }

        outputMaster.println(COMANDO_ADD + " " + nomeFile);
        outputMaster.flush();

        String risposta = inputMaster.nextLine().trim();

        if ("aggiunto".equalsIgnoreCase(risposta)) {
            System.out.println("File " + nomeFile + " aggiunto con successo");
        } else {
            System.out.println("Errore. Aggiunta file " + nomeFile + " non comunicata al master");
        }
    }

    // Gestore del comando quit
    private static void gestisciQuit(PrintWriter outputMaster) {
        outputMaster.println(COMANDO_QUIT);
        outputMaster.flush();
        System.out.println("Disconnessione in corso");
        server.terminaServer(); // termina PeerServer
    }

    // Crea un thread che esegua PeerServer
    private static void avvioServer() {
        latch = new CountDownLatch(1); // inizializza un blocco di attesa di 1 evento
        server = new PeerServer(latch);
        Thread threadServer = new Thread(server);
        threadServer.start();
    }
}

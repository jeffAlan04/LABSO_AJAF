import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    private static PeerServer server;
    private static final int PORTA_PEER_SERVER = 9999;

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Utilizzo: java Client [indirizzo master] [porta]");
            return;
        }

        // Blocco per ottenere l'indirizzo del peer
        String indirizzoIP;
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            indirizzoIP = localHost.getHostAddress();
        } catch (UnknownHostException e) {
            indirizzoIP = "localhost";
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

            List<String> risorseLocali = registrazioneRisorseLocali();

            for (String riga : risorseLocali) {
                outputMaster.println(riga);
                outputMaster.flush();
            }

            // Il client aspetta i comandi dell'utente
            while (true) {
                    String messaggio = tastiera.nextLine().trim();

                    switch(messaggio.toUpperCase()){
                        case "QUIT":
                            gestisciQuit(outputMaster);
                            break;

                        case "LISTDATA_LOCAL": 
                            GestioneRisorse.eseguiListDataLocal();
                            break;
                        
                        case "DOWNLOAD":
                            gestisciDownload(messaggio, inputMaster, outputMaster, PORTA_PEER_SERVER);
                            break;

                        case "ADD":
                            gestisciAdd(messaggio, outputMaster);
                            break;

                        case "LISTDATA_REMOTE" :
                            gestisciListDataRemote(outputMaster, inputMaster);
                            break;

                        default:
                            System.out.println("Comando non riconosciuto: " + messaggio);
                            break;
                    }
                }
            }
        catch (IOException e) {
            System.out.println("Errore di connessione al master");
        }
    }

    // Gestore del comando listdata_remote
    private static void gestisciListDataRemote(PrintWriter outputMaster, Scanner inputMaster) {
        outputMaster.println("LISTDATA_REMOTE");
        outputMaster.flush();

        if (inputMaster.hasNextLine()) {
            String risposta = inputMaster.nextLine();
            GestioneRisorse.eseguiListDataRemote(risposta);
        } else {
            System.out.println("Nessuna risposta ricevuta");
        }
    }
    
    // Gestore del comando download
    private static void gestisciDownload(String messaggio, Scanner inputMaster, PrintWriter outputMaster,
            int portaPeerServer) {

        String[] parti = messaggio.split("\\s+");
        if (parti.length != 2) {
            System.out.println("Uso corretto: download <nome_risorsa>");
            return;
        }
        String nomeRisorsa = parti[1];
        outputMaster.println("DOWNLOAD " + nomeRisorsa);
        outputMaster.flush();

        // Legge la risposta del master che contiene l'indirizzo dell'host peer che
        // possiede la risorsa indicata.
        String indirizzoHostPeer = inputMaster.nextLine();
        while (!"non_disponibile".equals(indirizzoHostPeer)) {
            indirizzoHostPeer = indirizzoHostPeer.split(":")[0];

            PeerClient pc = new PeerClient(indirizzoHostPeer, PORTA_PEER_SERVER, nomeRisorsa);
            if (pc.avviaConnessione()) {
                outputMaster.println("true");
                outputMaster.flush();
                break;
            }

            outputMaster.println("false");
            outputMaster.flush();
            indirizzoHostPeer = inputMaster.nextLine();
        }
        if ("non_disponibile".equals(indirizzoHostPeer)) {
            System.out.println("Download fallito: nessun peer disponibile");
        } else {
            System.out.println("Download avvenuto con successo");
        }
    }
    
    // Gestore del comando add
    private static void gestisciAdd(String messaggio, PrintWriter outputMaster){
        String[] parti = messaggio.split("\\s+", 3);
        if (parti.length < 3) {
            System.out.println("Uso corretto: add nome_risorsa contenuto");
            return;
        } 
        String nomeFile = parti[1];
        String contenuto = parti[2];

        if (!nomeFile.endsWith(".txt")){
            nomeFile += ".txt";
        }
        GestioneRisorse.eseguiAdd(nomeFile, contenuto);

        outputMaster.println("ADD " + nomeFile);
        outputMaster.flush();
    }

    // Gestore del comando quit
    private static void gestisciQuit(PrintWriter outputMaster) {
        outputMaster.println("QUIT");
        outputMaster.flush();
        System.out.println("Disconnessione in corso");
        server.terminaServer(); // termina PeerServer
    }

    public static List<String> registrazioneRisorseLocali() {
        List<String> risorseLocali = new ArrayList<>();

        risorseLocali.add("REGISTRAZIONE_RISORSE ");

        // All'interno della cartella risorse, vengono aggiunti alla lista.
        File cartella = new File("risorse/");
        if (cartella.exists() && cartella.isDirectory()) {
            File[] f = cartella.listFiles();

            if (f != null) {
                for (File file : f) {
                    if (file.isFile()) {
                        risorseLocali.add(file.getName());
                    }
                }
            }
        } else {
            System.out.println("Cartella risorse non trovata");
        }

        risorseLocali.add("FINE");
        return risorseLocali;
    }

    // Crea un thread che esegua PeerServer
    private static void avvioServer() {
        server = new PeerServer(PORTA_PEER_SERVER);
        Thread threadServer = new Thread(server);
        threadServer.start();
    }
}
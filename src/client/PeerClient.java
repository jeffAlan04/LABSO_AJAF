import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class PeerClient {

    private String indirizzoHostPeer;
    private int porta;
    private String indirizzoCompletoPeer;
    private String nomeRisorsa;
    private Logger logger;
    private final String CARTELLA_RISORSE = "risorse/";

    public PeerClient(String indirizzoHostPeer, int porta, String nomeRisorsa) {
        this.indirizzoHostPeer = indirizzoHostPeer;
        this.porta = porta;
        this.indirizzoHostPeerCompleto = indirizzoHostPeer + ":" + porta;
        this.nomeRisorsa = nomeRisorsa;
        this.logger = new Logger("PeerClient");
    }

    public boolean avviaConnessione() {
        try (Socket s = new Socket()) {
            s.connect(new java.net.InetSocketAddress(indirizzoHostPeer, porta), 5000);
            logger.logInfo("Connesso a " + indirizzoHostPeerCompleto);
            return richiediRisorsa(s);
        } catch (IOException e) {
            logger.logErrore("Tentativo di connessione al peer " + indirizzoHostPeerCompleto + " fallito");
            return false;
        }
    }

    private boolean richiediRisorsa(Socket s) {
        try (Scanner socketOut = new Scanner(s.getInputStream());
                PrintWriter socketIn = new PrintWriter(s.getOutputStream());) {

            logger.logInfo("Richiesta risorsa: " + nomeRisorsa);

            socketIn.println(nomeRisorsa);
            socketIn.flush();

            String rispostaServer = socketOut.nextLine();

            if (rispostaServer.equals("false")) {
                logger.logInfo("Il peer " + indirizzoHostPeerCompleto + " non possiede la risorsa " + nomeRisorsa);
                return false;
            }

            return downloadRisorsa(s, nomeRisorsa);

        } catch (IOException e) {
            logger.logErrore("Errore nel controllo della risorsa");
            return false;
        } finally {
            logger.logInfo("Disconnesione dal peer " + indirizzoHostPeerCompleto);
        }
    }

    private boolean downloadRisorsa(Socket s, String nomeRisorsa) {
        logger.logInfo("Inizio download risorsa " + nomeRisorsa + " da " + indirizzoHostPeerCompleto);

        try (InputStream is = s.getInputStream();
                FileOutputStream fos = new FileOutputStream(CARTELLA_RISORSE + nomeRisorsa);
                BufferedOutputStream bos = new BufferedOutputStream(fos);) {

            byte[] byteArray = new byte[4096]; // Buffer per la scrittura del file in locale
            int byteRead; // indica il numero di byte letti nel ciclo
            while ((byteRead = is.read(byteArray)) != -1) {
                // preleva i dati da byteArray dall'indice 0 a quello di byteRead e li scrive
                bos.write(byteArray, 0, byteRead);
            }
            bos.flush();
            logger.logInfo("Fine download risorsa " + nomeRisorsa + " da " + indirizzoHostPeerCompleto);
            return true;

        } catch (IOException e) {
            logger.logErrore("Errore nel download della risorsa " + nomeRisorsa);
            return false;
        }

    }

    // Da eliminare, solo per testing
    public static void main(String[] args) {
        PeerClient c;
        if (args.length < 2) {
            c = new PeerClient("localhost", 9999, "prova.txt");
        } else {
            c = new PeerClient(args[0], Integer.parseInt(args[1]), args[2]);
        }
        c.avviaConnessione();
    }

}

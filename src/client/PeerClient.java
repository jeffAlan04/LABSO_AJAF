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
    private String nomeRisorsa;
    private Logger logger;

    public PeerClient(String indirizzoHostPeer, int porta, String nomeRisorsa) {
        this.indirizzoHostPeer = indirizzoHostPeer;
        this.porta = porta;
        this.nomeRisorsa = nomeRisorsa;
        this.logger = new Logger("PeerClient");
    }

    public boolean avviaConnessione() {

        logger.logInfo("Tentativo di download della risorsa " + nomeRisorsa + " dal peer " + indirizzoHostPeer);

        try (Socket s = new Socket(indirizzoHostPeer, porta)) {

            logger.logInfo("Stabilita connessione con peer " + indirizzoHostPeer);
            return richiediRisorsa(s);

        } catch (IOException e) {

            logger.logErrore("Errore nella connessione al peer " + indirizzoHostPeer);
            return false;
        }

    }

    private boolean richiediRisorsa(Socket s) {
        try (Scanner socketOut = new Scanner(s.getInputStream());
                PrintWriter socketIn = new PrintWriter(s.getOutputStream());) {

            logger.logInfo("Richista risorsa " + nomeRisorsa);

            socketIn.println(nomeRisorsa);
            socketIn.flush();

            String rispostaServer = socketOut.nextLine();

            if (rispostaServer.equals("false")) {
                logger.logInfo("Il peer " + indirizzoHostPeer + " non possiede la risorsa " + nomeRisorsa);
                return false;
            }

            return downloadRisorsa(s, nomeRisorsa);

        } catch (IOException e) {
            logger.logErrore("Errore nel controllo della risorsa");
            return false;
        } finally {
            logger.logInfo("Disconnnesione");
        }
    }

    private boolean downloadRisorsa(Socket s, String nomeRisorsa) {
        logger.logInfo("Doownload della risorsa " + nomeRisorsa + "iniziato");

        try (InputStream is = s.getInputStream();
                FileOutputStream fos = new FileOutputStream("scaricati/" + nomeRisorsa);
                BufferedOutputStream bos = new BufferedOutputStream(fos);) {

            byte[] byteArray = new byte[4096]; // Buffer per la scrittura del file in locale
            int byteRead; // indica il numero di byte letti nel ciclo
            while ((byteRead = is.read(byteArray)) != -1) {
                // preleva i dati da byteArray dall'indice 0 a quello di byteRead e li scrive
                bos.write(byteArray, 0, byteRead);
            }
            bos.flush();
            logger.logInfo("Download terminato. Risorsa " + nomeRisorsa + " scaricata");
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

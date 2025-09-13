import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class PeerServer implements Runnable {
    private int porta;
    private ServerSocket serverSocket;
    private boolean running;
    private Logger logger;
    private final String CARTELLA_RISORSE = "risorse/";

    public PeerServer() {
        this.logger = new Logger("PeerServer");
    }

    @Override
    public void run() {
        try {
            this.serverSocket = new ServerSocket(0);
            this.porta = serverSocket.getLocalPort();
            logger.logInfo("Server in ascolto sulla porta " + porta);
            running = true;

            while (running) {

                try (Socket socket = serverSocket.accept()) {
                    String indirizzoPeer = socket.getRemoteSocketAddress().toString();
                    logger.logInfo("Connessione a " + indirizzoPeer);

                    avviaComunicazione(socket);

                    logger.logInfo("Chiusura connessione con peer " + indirizzoPeer);

                } catch (IOException e) {
                    if (!running) {
                        logger.logInfo("PeerServer chiuso");
                    } else {
                        logger.logErrore("Errore mentre veniva stabilita una connessione");
                    }
                }

            }
        } catch (IOException e) {
            logger.logErrore("Errore nell'esecuzione del server");
        } finally {
            terminaServer();
        }
    }

    private void avviaComunicazione(Socket s) {
        try (Scanner scanner = new Scanner(s.getInputStream());
                PrintWriter writer = new PrintWriter(s.getOutputStream())) {

            String nomeRisorsa = scanner.nextLine();
            String risposta = GestioneRisorse.risorsaPresente(nomeRisorsa);

            logger.logInfo("Disponibilità risorsa " + nomeRisorsa + ": " + risposta);
            writer.println(risposta);
            writer.flush();

            if (risposta.equals("true")) {
                uploadRisorsa(s, nomeRisorsa);

            }

        } catch (IOException e) {
            logger.logErrore(
                    "Errore durante controllo presenza di una risorsa con peer " + s.getRemoteSocketAddress());
        }
    }

    private void uploadRisorsa(Socket s, String nomeRisorsa) {
        File f = new File(CARTELLA_RISORSE + nomeRisorsa);
        byte[] byteArray = new byte[(int) f.length()];

        try (FileInputStream fis = new FileInputStream(f);
                BufferedInputStream bis = new BufferedInputStream(fis);
                OutputStream os = s.getOutputStream()) {

            logger.logInfo("Inizio upload risorsa " + nomeRisorsa + " verso peer " + s.getRemoteSocketAddress());
            bis.read(byteArray, 0, byteArray.length);
            os.write(byteArray, 0, byteArray.length);
            os.flush();
            logger.logInfo("Fine upload risorsa " + nomeRisorsa + " verso peer " + s.getRemoteSocketAddress());

        } catch (IOException e) {
            logger.logErrore("Errore durante upload della risorsa " + nomeRisorsa);
        }
    }

    public int getPorta() {
        return this.porta;
    }

    public void terminaServer() {
        running = false;

        try {
            if (serverSocket != null && !serverSocket.isClosed())
                serverSocket.close();
        } catch (IOException e) {
            logger.logErrore("Errore durante la chiusura del server");
        }
    }

    // Da eliminare, inserito per testing
    public static void main(String[] args) {
        PeerServer s;
        if (args.length < 1) {
            s = new PeerServer(9999);
        } else {
            s = new PeerServer(Integer.parseInt(args[0]));

        }
        s.run();
    }
}

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
import java.util.concurrent.CountDownLatch;

public class PeerServer implements Runnable {
    private int porta;
    private ServerSocket serverSocket;
    private boolean running;
    private Logger logger;
    private CountDownLatch latch;

    private final String CARTELLA_RISORSE = "risorse/";

    public PeerServer(CountDownLatch latch) {
        this.logger = new Logger("PeerServer");
        this.latch = latch; // meccanismo di sincronizzazione per trasmissione porta
    }

    @Override
    public void run() {
        try {
            this.serverSocket = new ServerSocket(0); // la porta viene stabilita dinamicamente in base a quelle
                                                     // disponibili
            this.porta = serverSocket.getLocalPort();
            latch.countDown(); // segnala che la porta e' stata determinata
            logger.logInfo("Server in ascolto sulla porta " + porta);
            running = true;

            while (running) {

                try (Socket socket = serverSocket.accept()) {
                    String indirizzoPeer = socket.getRemoteSocketAddress().toString();
                    logger.logInfo("Connessione a " + indirizzoPeer);

                    avviaComunicazione(socket);

                    logger.logInfo("Chiusura connessione con peer " + indirizzoPeer);

                } catch (IOException e) {
                    if (!running) { // in caso di eccezione dovuta a chiusura socket provocata da terminaServer()
                        logger.logInfo("Server chiuso");
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

    // metodo per stabilire la risorsa di cui fare l'upload
    private void avviaComunicazione(Socket s) {
        try (Scanner scanner = new Scanner(s.getInputStream());
                PrintWriter writer = new PrintWriter(s.getOutputStream())) {

            String nomeRisorsa = scanner.nextLine();
            String risposta = GestioneRisorse.risorsaPresente(nomeRisorsa); // disponibilità della risorsa per l'upload

            logger.logInfo("Disponibilità risorsa " + nomeRisorsa + ": " + risposta);
            writer.println(risposta); // comunica disponibilità risorsa al client
            writer.flush();

            if (risposta.equals("true")) {
                uploadRisorsa(s, nomeRisorsa); // procede con upload della risorsa
            }

        } catch (IOException e) {
            logger.logErrore(
                    "Errore durante controllo presenza di una risorsa con peer " + s.getRemoteSocketAddress());
        }
    }

    // metodo per upload della risorsa al client
    private void uploadRisorsa(Socket s, String nomeRisorsa) {
        File f = new File(CARTELLA_RISORSE + nomeRisorsa);
        byte[] byteArray = new byte[(int) f.length()]; // buffer per invio del file di dimensioni uguali a quelle del
                                                       // file

        try (FileInputStream fis = new FileInputStream(f);
                BufferedInputStream bis = new BufferedInputStream(fis);
                OutputStream os = s.getOutputStream()) {

            logger.logInfo("Inizio upload risorsa " + nomeRisorsa + " verso peer " + s.getRemoteSocketAddress());
            bis.read(byteArray, 0, byteArray.length); // legge il contenuto del file
            os.write(byteArray, 0, byteArray.length); // scrive al client il contenuto
            os.flush();
            logger.logInfo("Fine upload risorsa " + nomeRisorsa + " verso peer " + s.getRemoteSocketAddress());

        } catch (IOException e) {
            logger.logErrore("Errore durante upload della risorsa " + nomeRisorsa);
        }
    }

    // restituisce la porta su cui è in ascolto il server
    public int getPorta() {
        return this.porta;
    }

    // metodo per chiusura del server
    public void terminaServer() {
        running = false; // blocca il ciclo di accettazione connessioni

        try {
            if (serverSocket != null && !serverSocket.isClosed())
                serverSocket.close(); // chiude la socket
        } catch (IOException e) {
            logger.logErrore("Errore durante la chiusura del server");
        }
    }
}

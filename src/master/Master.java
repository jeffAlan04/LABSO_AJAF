import java.util.*;
import java.io.*;
import java.net.*;

public class Master{

    private static Socket socket;
    private static GestioneTab tabella;
    private static ArbitroLettoreScrittore arbitroTabella;
    private static ArbitroLettoreScrittore arbitroLog;
    private static Log log;

    public static boolean inEsecuzione = true;

    public static void main(String[] args) throws IOException {
        if (args.length < 1){
            System.out.println("Utilizzo: java Master <porta>");
            return;
        }
            
            int porta = Integer.parseInt(args[0]);

            tabella = new GestioneTab();
            arbitroTabella = new ArbitroLettoreScrittore();
            arbitroLog = new ArbitroLettoreScrittore();
            log = new Log();

            // Avvio del thread di GestioneComandi
            new Thread(new GestioneComandi(tabella, arbitroTabella, arbitroLog, log)).start();

        // Creazionde del ServerSocket    
        try (ServerSocket serverSocket = new ServerSocket(porta)){
        
            System.out.println("Server in ascolto sulla porta: " + porta);

            // Ciclo continuo fino a che inEsecuzione = false
            while(inEsecuzione){
                socket = serverSocket.accept();
                
                System.out.println("Connessione a: " + socket.getRemoteSocketAddress());

                // Creazione di GestorePeer e avvio del thread
                GestionePeer gp = new GestionePeer(socket, tabella, arbitroTabella, arbitroLog, log);
                new Thread(gp).start();
            }
        }
        
        catch(IOException e){
            System.err.println("Errore: " + e.getMessage());
        }    
    
    }
}
import java.util.*;
import java.io.*;
import java.net.*;

public class Master{

    private static Socket socket;
    private static GestioneTab tabella;
    private static ArbitroLetturaScrittura arbitroTabella;
    private static ArbitroLetturaScrittura arbitroLog;
    private static Log log;
    protected static List<GestionePeer> listaGestoriPeer = Collections.synchronizedList(new ArrayList<>()); 

    public static boolean inEsecuzione = true;

    public static void main(String[] args) throws IOException {
        if (args.length < 1){
            System.out.println("Utilizzo: java Master <porta>");
            return;
        }
            
            int porta = Integer.parseInt(args[0]);

            tabella = new GestioneTab();
            arbitroTabella = new ArbitroLetturaScrittura();
            arbitroLog = new ArbitroLetturaScrittura();
            log = new Log();

            // Avvio del thread di GestoreComandi
            new Thread(new GestoreComandi(arbitroLog, arbitroTabella, tabella, log)).start();

        // Creazionde del ServerSocket    
        try (ServerSocket serverSocket = new ServerSocket(porta)){
        
            System.out.println("Server in ascolto sulla porta: " + porta);

            // Ciclo continuo fino a che inEsecuzione = false
            while(inEsecuzione){
                socket = serverSocket.accept();
                
                System.out.println("Connessione a: " + socket.getRemoteSocketAddress());

                // Creazione di GestionePeer e avvio del thread
                GestionePeer gp = new GestionePeer(socket, log, arbitroTabella, arbitroLog, tabella);
                synchronized (listaGestoriPeer) {
                    listaGestoriPeer.add(gp);
                }
                new Thread(gp).start();
            }
        }
        
        catch(IOException e){
            System.err.println("Errore: " + e.getMessage());
        }    
    
    }
}
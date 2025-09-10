import java.util.*;
import java.io.*;
import java.net.*;

public class Master{

    private static Socket socket;
    private static GestioneTab tabella;
    private static ArbitroLetturaScrittura arbitroTabella;
    private static ArbitroLetturaScrittura arbitroLog;
    private static LogMaster logMaster;
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
        logMaster = new LogMaster();

        // Creazionde del ServerSocket    
        try (ServerSocket serverSocket = new ServerSocket(porta)){
            System.out.println("Server in ascolto sulla porta: " + porta);

            // Avvio del thread di GestoreComandi
            new Thread(new GestoreComandi(arbitroLog, arbitroTabella, tabella, logMaster, serverSocket)).start();

            // Ciclo continuo fino a che inEsecuzione = false
            while(inEsecuzione){
                socket = serverSocket.accept();
                
                System.out.println("Connessione a: " + socket.getRemoteSocketAddress());

                // Creazione di GestionePeer e avvio del thread
                GestionePeer gp = new GestionePeer(socket, logMaster, arbitroTabella, arbitroLog, tabella);
                synchronized (listaGestoriPeer) {
                    listaGestoriPeer.add(gp);
                }
                new Thread(gp).start();
            }
        }
        
        catch(IOException e){
            System.out.println("Chiusura master.");
        }
    }
}
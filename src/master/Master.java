import java.util.*;
import java.io.*;
import java.net.*;

public class Master{

    private static Socket socket;
    private static GestioneTab tabella;
    private static ReaderWriter lettore;
    private static ReaderWriter scrittore;
    public static void main(String[] args) throws IOException {
        if (args.length < 1){
            System.out.println("Utilizzo: java Master <porta>");
            return;
        }
            
            int porta = Integer.parseInt(args[0]);

            tabella = new GestioneTab();
            lettore = new ReaderWriter();
            scrittore = new ReaderWriter();
            
        try (ServerSocket serverSocket = new ServerSocket(porta);){
        
            System.out.println("Server in ascolto sulla porta: " + porta);

            while(true){
                socket = serverSocket.accept();
                System.out.println("Connessione a: " + socket.getRemoteSocketAddress());
                GestorePeer gp = new GestorePeer(socket, tabella, lettore, scrittore);
                new Thread(gp).start();
            }
        }
        
        catch(IOException e){
            System.err.println("Errore: " + e.getMessage());
        }    
    
    }
}
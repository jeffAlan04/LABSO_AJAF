package master;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.*;

public class MasterServer {
    // Tabella delle risorse condivise tra i peer
    private final TabellaRisorse tabella = new TabellaRisorse();
    // Thread pool per gestire i peer in parallelo
    private final ExecutorService pool = Executors.newCachedThreadPool();
    // Lista thread-safe dei log dei download
    private static final List<Log> logDownload = Collections.synchronizedList(new ArrayList<>());
    // Mappa per associare ID peer a nomi univoci
    private static final Map<String, String> peerNomi = new HashMap<>(); // aggiungi qui
    // Contatore per generare nomi univoci tipo peer0, peer1
    private static int counterPeer = 0; // aggiungi qui

    // Metodo per registrare un peer e dargli un nome unico tipo peer0, peer1
    public static synchronized String registraPeer(String idPeer){
        if(!peerNomi.containsKey(idPeer)){
            String nome = "peer" + counterPeer++;
            peerNomi.put(idPeer, nome);
        }
        return peerNomi.get(idPeer);
    }

    // Metodo per recuperare il nome di un peer a partire dal suo id
    public static String getNomePeer(String idPeer){
        return peerNomi.getOrDefault(idPeer, idPeer);
    }

    // Avvia il server master sulla porta specificata
    public void avvia(int porta) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(porta)) {
            System.out.println("master in ascolto sulla porta " + porta);

            // Ciclo infinito per accettare nuovi peer
            while (true) {
                Socket socketPeer = serverSocket.accept();
                System.out.println("nuovo peer connesso: " + socketPeer.getInetAddress());

                // Esegue la gestione del peer in un thread separato
                pool.execute(new GestorePeer(socketPeer, tabella));
            }
        }
    }

    // Aggiunge una riga di log dei download
    public static void aggiungiLog(Log rigaLog) {
        logDownload.add(rigaLog);
    }

    // Restituisce l'intera lista dei log
    public static List<Log> getLog(){
        return logDownload;
    }

    // Test di avvio del server (da eliminare poi)
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Uso corretto: java master.MasterServer <porta>");
            System.out.println("Esempio: java master.MasterServer 12345");
            return;
        }

        try {
            int porta = Integer.parseInt(args[0]);
            new MasterServer().avvia(porta);
        } catch (NumberFormatException e) {
            System.out.println("Errore: la porta deve essere un numero intero.");
        } catch (IOException e) {
            System.out.println("Errore durante l'avvio del server: " + e.getMessage());
        }
    }
}

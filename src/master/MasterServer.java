package master;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class MasterServer {
    // Tabella delle risorse condivise tra i peer
    private final TabellaRisorse tabella = new TabellaRisorse();
    // Thread pool per gestire i peer in parallelo
    private final ExecutorService pool = Executors.newCachedThreadPool();
    // Mappa per associare ID peer a nomi univoci
    private static final java.util.Map<String, String> peerNomi = new java.util.HashMap<>();
    private static int counterPeer = 0;

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

            while (true) {
                Socket socketPeer = serverSocket.accept();
                System.out.println("nuovo peer connesso: " + socketPeer.getInetAddress());
                pool.execute(new GestorePeer(socketPeer, tabella));
            }
        }
    }

    // Aggiunge una riga di log solo su file
    public static void aggiungiLog(Log rigaLog) {
        try(FileWriter fw = new FileWriter("/home/james/Scrivania/LABSO_AJAF/src/master/log.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw)) {
            out.println(rigaLog.toString());
        } catch (IOException e) {
            System.err.println("Errore scrittura log su file: " + e.getMessage());
        }
    }

    // Test di avvio del server
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Uso corretto: java master.MasterServer <porta>");
            return;
        }

        try {
            int porta = Integer.parseInt(args[0]);
            new MasterServer().avvia(porta);
        } catch (Exception e) {
            System.out.println("Errore durante l'avvio del server: " + e.getMessage());
        }
    }
}

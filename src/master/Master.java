package master;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class MasterServer {
    private final TabellaRisorse tabella = new TabellaRisorse();
    private final ExecutorService pool = Executors.newCachedThreadPool();
    private static final Map<String, String> peerNomi = new HashMap<>();
    private static int counterPeer = 0;

    public static synchronized String registraPeer(String idPeer){
        if(!peerNomi.containsKey(idPeer)){
            String nome = "peer" + counterPeer++;
            peerNomi.put(idPeer, nome);
        }
        return peerNomi.get(idPeer);
    }

    public static String getNomePeer(String idPeer){
        return peerNomi.getOrDefault(idPeer, idPeer);
    }

    public void avvia(int porta) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(porta)) {
            System.out.println("Master in ascolto sulla porta " + porta);

            while (true) {
                Socket socketPeer = serverSocket.accept();

                // ID e nome del peer
                String idPeer = socketPeer.getRemoteSocketAddress().toString();
                String nomePeer = registraPeer(idPeer);
                System.out.println("Nuovo peer connesso: " + nomePeer + " (ID: " + idPeer + ")");

                // Avvio handler per il peer
                pool.execute(new GestorePeer(socketPeer, tabella));
            }
        }
    }

    public static void aggiungiLog(Log rigaLog) {
        try (FileWriter fw = new FileWriter("log.txt", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(rigaLog.toString());
        } catch (IOException e) {
            System.err.println("Errore scrittura log: " + e.getMessage());
        }
    }

    public static void notificaDisconnessione(String idPeer) {
        String nomePeer = getNomePeer(idPeer);
        System.out.println("Peer disconnesso: " + nomePeer + " (ID: " + idPeer + ")");
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Uso corretto: java master.MasterServer <porta>");
            return;
        }

        try {
            int porta = Integer.parseInt(args[0]);
            MasterServer server = new MasterServer();

            // Avvio server in thread separato
            new Thread(() -> {
                try {
                    server.avvia(porta);
                } catch (IOException e) {
                    System.err.println("Errore avvio server: " + e.getMessage());
                }
            }).start();

            // Console interattiva
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String comando = scanner.nextLine().trim();

                if (comando.equalsIgnoreCase("listdata")) {
                    Map<String, Set<String>> risorse = server.tabella.ottieniTutteRisorse();
                    if (risorse.isEmpty()) {
                        System.out.println("Nessuna risorsa disponibile.");
                    } else {
                        System.out.println("Risorse disponibili:");
                        for (var entry : risorse.entrySet()) {
                            String nomeRisorsa = entry.getKey();
                            Set<String> peers = entry.getValue();
                            String listaPeer = peers.isEmpty() ? "non disponibile" : String.join(", ", peers);
                            System.out.println("- " + nomeRisorsa + ": " + listaPeer);
                        }
                    }

                } else if (comando.equalsIgnoreCase("log")) {
                    try (BufferedReader br = new BufferedReader(new FileReader("log.txt"))) {
                        System.out.println("Risorse scaricate:");
                        String line;
                        while ((line = br.readLine()) != null) {
                            System.out.println("- " + line);
                        }
                    } catch (IOException e) {
                        System.err.println("Errore lettura log: " + e.getMessage());
                    }

                } else if (comando.equalsIgnoreCase("quit")) {
                    System.out.println("Arresto del master...");
                    System.exit(0);

                } else {
                    System.out.println("Comando non riconosciuto: " + comando);
                }
            }

        } catch (Exception e) {
            System.err.println("Errore: " + e.getMessage());
        }
    }
}

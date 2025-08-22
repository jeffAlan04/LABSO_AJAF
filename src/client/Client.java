import java.io.*;
import java.net.*;
import java.util.*;

public class Client {

  private static PeerServer server;

  public static void main(String[] args) {
    if (args.length < 2) {
      System.out.println("Utilizzo: java Client [indirizzo master] [porta]");
      return;
    }

    String indirizzoMaster = args[0];
    int porta = Integer.parseInt(args[1]);

    try (
        Socket s = new Socket(indirizzoMaster, porta);
        Scanner inputMaster = new Scanner(s.getInputStream());
        PrintWriter outputMaster = new PrintWriter(s.getOutputStream());
        Scanner tastiera = new Scanner(System.in);) {
      System.out.println("Connessione al master: " + s.getRemoteSocketAddress());

      avvioServer(); // Avvia PeerServer in contemporanea

      // Il client aspetta i comandi dell'utente
      while (true) {
        System.out.print("> ");
        String inputUtente = tastiera.nextLine().trim();

        if (inputUtente.equalsIgnoreCase("quit")) {
          eseguiQuit(outputMaster);
          break;

        }

        // Esegue il comando listdata local
       else if (inputUtente.equalsIgnoreCase("listdata local")){
          GestioneRisorse.eseguiListDataLocal();
          break;
       }

       // Esegue il comando add nome_risorsa contenuto
       else if (inputUtente.startsWith("add ")){
        String[] parti = inputUtente.split("\\s", 3);
        if (parti.length < 3){
          System.out.println("Uso corretto: add nome_risorsa contenuto");
        }
        else{
          String nomeFile = parti[1];

          // Se nome_risorsa inserito dall'utente non contiene ".txt", viene aggiunto.
          if (!nomeFile.contains(".txt")){
            nomeFile = nomeFile + ".txt";
          }
          String contenuto = parti[2];

          GestioneRisorse.eseguiAdd(nomeFile, contenuto);

          outputMaster.println("ADD " + nomeFile);
          outputMaster.flush();
        }
       }

       // Esegue il comando donwload nome_risorsa.
       else if (inputUtente.startsWith("download")){
        String[] parti = inputUtente.split("\\s");
        if (parti.length != 2){
          System.out.println("Uso corretto: download nome_risorsa");
        }
        else{
          String nomeRisorsa = parti[1];

          // Se nome_risorsa inserito dall'utente non contiene ".txt", viene aggiunto.
          if (!nomeRisorsa.contains(".txt")){
            nomeRisorsa = nomeRisorsa + ".txt";
          }

          outputMaster.println("DOWNLOAD " + nomeRisorsa);
          outputMaster.flush();

          // Legge la risposta del master che contiene l'indirizzo dell'host peer che possiede la risorsa indicata.
          String indirizzoHostPeer = inputMaster.nextLine();

          PeerClient pc = new PeerClient(indirizzoHostPeer, porta, nomeRisorsa);

          boolean risorsaTrovata = pc.avviaConnessione();

          // Fino a che risorsaTrovata non corrisponde a true, il master deve fornire un host peer alternativo, a meno che questo sia "NESSUNO" uscendo dal ciclo.
          while (!risorsaTrovata){
            outputMaster.println("Risorsa non disponibile dal Peer: " + indirizzoHostPeer);
            outputMaster.flush();

            String indirizzoHostPeerAlternativo = inputMaster.nextLine();

            if (indirizzoHostPeerAlternativo.equals("NESSUNO")){
              System.out.println("Download fallito: nessun peer disponibile");
              break;
            }
            else{
              PeerClient pcAlternativo = new PeerClient(indirizzoHostPeerAlternativo, porta, nomeRisorsa);

              risorsaTrovata = pcAlternativo.avviaConnessione();
            }
            
          }
          
        }
        
       }
       
       // Da togliere non appena completati tutti i comandi
       else{
        System.out.println("Altri comandi");
       }

      }
    } catch (IOException e) {
      System.out.println("Errore di connessione al master");
    }
  }

  // Metodo per il comando quit
  private static void eseguiQuit(PrintWriter outputMaster) {
    outputMaster.println("QUIT");
    outputMaster.flush();
    System.out.println("Disconnessione in corso");

    server.terminaServer(); // termina PeerServer

  }

  // Crea un thread che esegua PeerServer
  private static void avvioServer() {
    server = new PeerServer(9999);
    Thread threadServer = new Thread(server);
    threadServer.start();
  }
}

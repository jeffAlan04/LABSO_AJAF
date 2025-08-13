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
<<<<<<< HEAD
        } else if (inputUtente.equalsIgnoreCase("listdata local")) {
          GestioneRisorse.eseguiListDataLocal();
          break;
        } else {
          System.out.println("Altri comandi");
        }
=======
        }

       else if (inputUtente.equalsIgnoreCase("listdata local")){
          GestioneRisorse.eseguiListDataLocal();
          break;
       }

       else if (inputUtente.startsWith("add ")){
        String[] parti = inputUtente.split("\\s", 3);
        if (parti.length < 3){
          System.out.println("Uso corretto: add nome_risorsa contenuto");
        }
        else{
          String nomeFile = parti[1];

          if (!nomeFile.contains(".txt")){
            nomeFile = nomeFile + ".txt";
          }
          String contenuto = parti[2];

          GestioneRisorse.eseguiAdd(nomeFile, contenuto);

          outputMaster.println("ADD " + nomeFile);
          outputMaster.flush();
        }
       }
       
       else{
        System.out.println("Altri comandi");
       }
>>>>>>> feat/client
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

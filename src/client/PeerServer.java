import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class PeerServer implements Runnable {
  private int porta;
  private ServerSocket serverSocket;

  public PeerServer(int porta) {
    this.porta = porta;
  }

  @Override
  public void run() {
    try {
      this.serverSocket = new ServerSocket(porta);
      System.out.println("Server in ascolto sulla porta " + porta);

      while (true) {

        try (Socket socket = serverSocket.accept()) {
          String indirizzoPeer = socket.getRemoteSocketAddress().toString();
          System.out.println("Connessione a " + indirizzoPeer);

          avviaComunicazione(socket);

          System.out.println("Chiusura connessione con peer " + indirizzoPeer);

        } catch (IOException e) {
          System.out.println("Errore mentre veniva stabilita una connessione");
        }

      }
    } catch (IOException e) {
      System.out.println("Errore nell'esecuzione del server");
    } finally {
      terminaServer();
    }
  }

  private synchronized void avviaComunicazione(Socket s) {
    try (Scanner scanner = new Scanner(s.getInputStream());
        PrintWriter writer = new PrintWriter(s.getOutputStream())) {

      String nomeRisorsa = scanner.nextLine();
      String risposta = GestioneRisorse.risorsaPresente(nomeRisorsa);

      System.out.println("Disponibilità risorsa " + nomeRisorsa + ": " + risposta);
      writer.println(risposta);

      if (risposta.equals("true")) {

      }

    } catch (IOException e) {
      System.out.println("Errore durante upload di una risorsa con peer " + s.getRemoteSocketAddress());
    }
  }

  public void terminaServer() {
    try {
      serverSocket.close();
    } catch (IOException e) {
      System.out.println("Errore durante la chiusura del server");
    }
  }

  public static void main(String[] args) {
    PeerServer s;
    if (args.length < 1) {
      s = new PeerServer(9999);
    } else {
      s = new PeerServer(Integer.parseInt(args[0]));

    }
    s.run();
  }
}

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

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
        Socket socket = serverSocket.accept();
        System.out.println("Connessione a " + socket.getRemoteSocketAddress());

      }
    } catch (IOException e) {
      System.out.println("Errore nell'esecuzione del server");
      terminaServer();
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

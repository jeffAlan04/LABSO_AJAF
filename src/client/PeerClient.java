import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class PeerClient {

  private String indirizzoHostPeer;
  private int porta;
  private String nomeRisorsa;

  public PeerClient(String indirizzoHostPeer, int porta, String nomeRisorsa) {
    this.indirizzoHostPeer = indirizzoHostPeer;
    this.porta = porta;
    this.nomeRisorsa = nomeRisorsa;
  }

  private boolean avviaConnessione() {

    try (Socket s = new Socket(indirizzoHostPeer, porta)) {

      System.out.println("Connesso al peer " + indirizzoHostPeer);
      return richiediRisorsa(s);

    } catch (IOException e) {

      System.out.println("Errore nella connessione al peer " + indirizzoHostPeer);
      return false;
    }

  }

  private boolean richiediRisorsa(Socket s) {
    try (Scanner socketOut = new Scanner(s.getInputStream());
        PrintWriter socketIn = new PrintWriter(s.getOutputStream());) {

      System.out.println("Richiedo risorsa " + nomeRisorsa);

      socketIn.println(nomeRisorsa);
      socketIn.flush();

      String rispostaServer = socketOut.nextLine();

      if (rispostaServer.equals("false")) {
        System.out.println("Risorsa " + nomeRisorsa + " non disponibile");
        System.out.println("Disconnessione");
        return false;
      }

      System.out.println("Disconnessione");
      return ricezioneRisorsa(s);

    } catch (IOException e) {
      System.out.println("Errore nel controllo della risorsa");
      return false;
    }
  }

  private boolean ricezioneRisorsa(Socket s) {
    System.out.println("Inizio il download della risorsa");
    return true;
  }

  // Da eliminare, solo per testing
  public static void main(String[] args) {
    PeerClient c;
    if (args.length < 2) {
      c = new PeerClient("localhost", 9999, "prova.txt");
    } else {
      c = new PeerClient(args[0], Integer.parseInt(args[1]), args[2]);
    }
    c.avviaConnessione();
  }

}

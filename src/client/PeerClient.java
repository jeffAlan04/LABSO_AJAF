import java.io.IOException;
import java.net.Socket;

public class PeerClient {

  private String indirizzoHostPeer;
  private int porta;
  private String nomeRisorsa;
  private Socket s;

  public PeerClient(String indirizzoPeer, int porta, String nomeRisorsa) {
    this.indirizzoHostPeer = indirizzoPeer;
    this.porta = porta;
    this.nomeRisorsa = nomeRisorsa;
  }

  private boolean avviaConnessione() {
    try {
      this.s = new Socket(indirizzoHostPeer, porta);
    } catch (IOException e) {
      System.out.println("Errore nella connessione al peer " + indirizzoHostPeer);
      return false;
    }

    return true;
  }

  private void terminaConnessione() {
    try {
      s.close();
    } catch (IOException e) {
      System.out.println("Errore nella chiusura della connessione con il peer " + indirizzoHostPeer);
    }
  }

}

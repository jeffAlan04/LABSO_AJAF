import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
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
      return ricezioneRisorsa(s, nomeRisorsa);

    } catch (IOException e) {
      System.out.println("Errore nel controllo della risorsa");
      return false;
    }
  }

  private boolean ricezioneRisorsa(Socket s, String nomeRisorsa) {
    System.out.println("Inizio il download della risorsa " + nomeRisorsa);
    try (InputStream is = s.getInputStream();
        FileOutputStream fos = new FileOutputStream("scaricati/" + nomeRisorsa);
        BufferedOutputStream bos = new BufferedOutputStream(fos);) {

      byte[] byteArray = new byte[4096]; // Buffer per la scrittura del file in locale
      int byteRead; // indica il numero di byte letti nel ciclo
      while ((byteRead = is.read(byteArray)) != -1) {
        // preleva i dati da byteArray dall'indice 0 a quello di byteRead e li scrive
        bos.write(byteArray, 0, byteRead);
      }
      bos.flush();
      System.out.println("Risorsa" + nomeRisorsa + " scaricata");
      return true;

    } catch (IOException e) {
      System.out.println("Errore nel download della risorsa " + nomeRisorsa);
      return false;
    }

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

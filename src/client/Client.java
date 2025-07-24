import java.io.*;
import java.net.*;
import java.util.Scanner;

//alMaster = outputMaster

public class Client {
  public static void main(String[] args) {
    if (args.length < 2) {
      System.out.println("Utilizzo: java Client [indirizzo master] [porta]");
      return;
    }

    String indirizzoMaster = args[0];
    int porta = Integer.parseInt(args[1]);

    try(
      Socket socket = new Socket(indirizzoMaster, porta);
      Scanner inputMaster = new Scanner(socket.getInputStream());
      PrintWriter outputMaster = new PrintWriter(socket.getOutputStream());
      Scanner tastiera = new Scanner(System.in);
    ){
      System.out.println("Connessione al master: " + socket.getRemoteSocketAddress());

      while (true) {
        System.out.print("> ");
        String inputUtente = tastiera.nextLine();
      }
    }
    catch(IOException e){
      System.out.println("Errore di connessione al master");
    }
  }
}
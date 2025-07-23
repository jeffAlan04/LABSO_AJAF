import java.io.*;
import java.util.Scanner;

public class Client {
  public static void main(String[] args) {
    if (args.length < 2) {
      System.out.println("Utilizzo: java Client [indirizzo master] [porta]");
      return;
    }

    String indirizzoMaster = args[0];
    int porta = Integer.parseInt(args[1]);

    Scanner tastiera = new Scanner(System.in);

    while (true) {
      System.out.print("> ");
      String inputUtente = tastiera.nextLine();
    }

  }
}

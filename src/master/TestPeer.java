package master;

import java.io.*;
import java.net.*;

public class TestPeer {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 12345);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            // Aggiungi una risorsa
            out.println("AGGIUNGI|R1");
            System.out.println(in.readLine());

            // Richiedi la lista delle risorse
            out.println("LISTA");
            System.out.println(in.readLine());

            // Disconnetti il peer
            out.println("QUIT");
            System.out.println(in.readLine());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

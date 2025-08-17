package master;

import java.io.*;
import java.net.*;

public class TestPeer {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 9000);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            // Aggiungi una risorsa
            out.println("AGGIUNGI|R1");
            System.out.println(in.readLine());

            // Richiedi la lista delle risorse
            out.println("LISTA");
            System.out.println(in.readLine());

            // Prova a fare un download
            out.println("DOWNLOAD|R1");
            System.out.println(in.readLine());

            // Richiedi i log dal server
            out.println("LOG");
            String risposta;
            while ((risposta = in.readLine()) != null && !risposta.isEmpty()){
                System.out.println(risposta);
            }
            // Disconnetti il peer
            out.println("QUIT");
            System.out.println(in.readLine());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

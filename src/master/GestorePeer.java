package master;

import java.io.*;
import java.net.*;

class GestorePeer implements Runnable {
    private final Socket socket;
    private final TabellaRisorse tabella;

    public GestorePeer(Socket socket, TabellaRisorse tabella){
        this.socket = socket;
        this.tabella = tabella;
    }

    @Override
    public void run(){
        try(
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)){

            String ipPeer = socket.getInetAddress().getHostAddress();
            String messaggio;

            while ((messaggio = in.readLine()) != null) {
                if(messaggio.startsWith("AGGIUNGI")) {
                    // es: "AGGIUNGI|R0"
                    String risorsa = messaggio.split("\\|")[1];
                    tabella.aggiungiRisorsa(risorsa, ipPeer);
                    out.println("OK: risorsa " + risorsa + " aggiunta");
                }
                else if(messaggio.startsWith("LISTA")) {
                    out.println("risorse disponinili: " + tabella.ottieniTutteRisorse());
                }
                else if(messaggio.equals("QUIT")) {
                    out.println("Peer " + ipPeer + " disconesso");
                    break;
                }
            }
        }catch (IOException e) {
            System.err.println(("Errore con peer: " + e.getMessage()));
        }
    }
}

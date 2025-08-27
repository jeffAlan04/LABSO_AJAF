import java.util.*;
import java.io.*;
import java.net.*;

public class GestionePeer implements Runnable {
    private final Socket socket;
    private final ArbitroLetturaScrittura arbitroTabella;
    private final ArbitroLetturaScrittura arbitroLog;
    private final GestioneTab gestioneTab;

    public GestionePeer(Socket socket, ArbitroLetturaScrittura arbitroLog, ArbitroLetturaScrittura arbitroTabella, GestioneTab gestioneTab) {
        this.socket = socket;
        this.arbitroLog = arbitroLog;
        this.arbitroTabella = arbitroTabella;
        this.gestioneTab = gestioneTab;
    }

    @Override
    public void run() {
        // comandi da gestire: listdata remote, quit, add risorsa, download risorsa
        try (Scanner in = new Scanner(socket.getInputStream()); PrintWriter out = new PrintWriter(socket.getOutputStream())) {
            while (in.hasNextLine()) {
                String comando = in.nextLine();
                if (comando.split(" ")[0].equals("LISTDATA_REMOTE")) {
                    out.println(listDataRemote());
                }
            }
        }
        catch (IOException e) {
            // ...
        }
    }
    
    private String listDataRemote() {
        try {
            this.arbitroTabella.inizioLettura();
            String risposta = this.gestioneTab.getRisorse();

            if (risposta == null || risposta.isEmpty()) {
                return "listdata remote: Nessuna risorsa disponibile.";
            }

            return risposta.trim();
        }
        finally {
            this.arbitroTabella.fineLettura();
        }
    }
}

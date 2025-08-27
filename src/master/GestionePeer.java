import java.util.*;
import java.io.*;
import java.net.*;

public class GestionePeer implements Runnable {
    private final Socket socket;
    private final ArbitroLetturaScrittura arbitroTabella;
    private final ArbitroLetturaScrittura arbitroLog;
    private final GestioneTab gestioneTab;

    private final String COMANDO_LISTDATAREMOTE = "LISTDATA_REMOTE";
    private final String COMANDO_QUIT = "QUIT";
    private final String COMANDO_ADD = "ADD";
    private final String COMANDO_DOWNLOAD = "DOWNLOAD";

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
                String[] richiesta = in.nextLine().split(" ");
                String comando = richiesta[0];
                String nomeRisorsa = null;
                if (richiesta.length > 1) {
                    nomeRisorsa = richiesta[1];
                }

                switch (comando) {
                    case COMANDO_LISTDATAREMOTE:
                        out.println(listDataRemote());
                        break;

                    case COMANDO_QUIT:
                        return;

                    case COMANDO_ADD:
                        String indirizzoIp = this.socket.getInetAddress().getHostAddress();

                        if (nomeRisorsa != null) {
                            out.println(addRisorsa(indirizzoIp, Set.of(nomeRisorsa)));
                        }
                        else {
                            out.println("Specifica una risorsa da aggiungere.");
                        }

                        break;

                    case COMANDO_DOWNLOAD:
                        break;

                    default:
                        return;
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

    private String addRisorsa(String indirizzoIp, Set<String> risorse) {
        try {
            this.arbitroTabella.inizioScrittura();
            return this.gestioneTab.aggiungiPeer(indirizzoIp, risorse).trim();
        }
        finally {
            this.arbitroTabella.fineScrittura();
        }
    }
}

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
    }
    
}

package master;

public class TestGestoreComandi {
    public static void main(String[] args) {
        // Creo gli arbitri
        ArbitroLetturaScrittura arbitroLog = new ArbitroLetturaScrittura();
        ArbitroLetturaScrittura arbitroTabella = new ArbitroLetturaScrittura();

        // Creo la tabella delle risorse
        GestioneTab tabella = new GestioneTab();

        // Recupero l’istanza del logger (singleton)
        Log logger = new Log();

        // Aggiungo alcune risorse di test
        tabella.aggiungiPeer("peer0", java.util.Set.of("R0", "R1"));
        tabella.aggiungiPeer("peer1", java.util.Set.of("R2"));

        // Scrivo alcuni log di test
        logger.downloadSuccesso("R0", "peer0", "peer1");
        logger.downloadFallito("R1", "peer1", "peer0");
        logger.downloadSuccesso("R2", "peer0", "peer1");

        // Avvio il thread dei comandi
     //   GestoreComandi gestore = new GestoreComandi(arbitroLog, arbitroTabella, tabella, logger);
       // new Thread(gestore).start();
    }
}

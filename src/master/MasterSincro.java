package master;

// Classe che implementa un meccanismo di sincronizzazione lettori-scrittori
// permette più lettori simultanei ma scrittori esclusivi
public class MasterSincro {

    private int lettoriAttivi;  // numero di lettori attivi
    private boolean inLettura;  // true se ci sono lettori attivi
    private boolean inScrittura; // true se c'è uno scrittore attivo

    public MasterSincro() {
        lettoriAttivi = 0;
        inLettura = false;
        inScrittura = false;
    }

    // Metodo per iniziare una lettura
    // se c'è uno scrittore, il lettore attende
    public synchronized int inizioLettura() {
        while (inScrittura) {
            try {
                wait();
            } catch (InterruptedException e) {
                // ignoriamo l'interruzione
            }
        }
        lettoriAttivi++;
        if (lettoriAttivi == 1)
            inLettura = true;
        return lettoriAttivi;
    }

    // Metodo per terminare una lettura
    // se non ci sono più lettori, sveglia eventuali scrittori in attesa
    public synchronized int fineLettura() {
        lettoriAttivi--;
        if (lettoriAttivi == 0)
            notifyAll();
        return lettoriAttivi;
    }

    // Metodo per iniziare una scrittura
    // attende se ci sono lettori o altri scrittori attivi
    public synchronized void inizioScrittura() {
        while (inLettura || inScrittura) {
            try {
                wait();
            } catch (InterruptedException e) {
                // ignoriamo l'interruzione
            }
        }
        inScrittura = true;
    }

    // Metodo per terminare una scrittura
    // sveglia tutti i thread in attesa
    public synchronized void fineScrittura() {
        inScrittura = false;
        notifyAll();
    }
}

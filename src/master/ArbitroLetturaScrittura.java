public class ArbitroLetturaScrittura {

    private int contatoreLettori; // numero di lettori attivi
    private boolean lettura; // true se almeno un lettore ha l'accesso
    private boolean scrittura; // true se uno scrittore ha l'accesso

    public ArbitroLetturaScrittura() {
        contatoreLettori = 0;
        lettura = false;
        scrittura = false;
    }

    public synchronized void inizioLettura() {
        while (scrittura == true) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // reimposta lo stato interrupt
                throw new IllegalStateException("Thread interrotto durante l'attesa per la lettura", e); // solleva
                                                                                                         // runtime
                                                                                                         // exception
                                                                                                         // per impedire
                                                                                                         // al thread di
                                                                                                         // entrare in
                                                                                                         // lettura
            }
        }

        contatoreLettori++;

        if (contatoreLettori == 1) {
            lettura = true;
        }

    }

    public synchronized void fineLettura() {
        if (contatoreLettori > 0) {
            --contatoreLettori;

            if (contatoreLettori == 0) {
                lettura = false;
                notifyAll();
            }
        }
    }

    public synchronized void inizioScrittura() {
        while (lettura == true || scrittura == true) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // reimposta lo stato interrupt
                throw new IllegalStateException("Thread interrotto durante l'attesa per la lettura", e); // solleva
                                                                                                         // runtime
                                                                                                         // exception
                                                                                                         // per impedire
                                                                                                         // al thread di
                                                                                                         // entrare in
                                                                                                         // lettura
            }
        }

        scrittura = true;

    }

    public synchronized void fineScrittura() {
        scrittura = false;
        notifyAll();
    }

}

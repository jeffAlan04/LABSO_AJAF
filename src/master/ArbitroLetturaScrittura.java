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
            }
        }

        scrittura = true;

    }

    public synchronized void fineScrittura() {
        scrittura = false;
        notifyAll();
    }

}

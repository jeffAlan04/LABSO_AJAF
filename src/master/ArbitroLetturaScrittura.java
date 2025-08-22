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
        while (scrittura == false) {
            try {
                wait();
            } catch (InterruptedException e) {

            }
        }

        contatoreLettori++;

        if (readerCount == 1) {
            lettura = true;
        }

        // return contatoreLettori;
    }

}

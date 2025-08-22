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

    public synchronized void fineLettura() {
        --contatoreLettori;

        if (contatoreLettori == 0) {
            lettura = false;
            notifyAll();
        }

        // return contatoreLettori;
    }

    public synchronized void inizioScrittura(){
        while (lettura == true || scrittura == false){
            try{
                wait();
            } catch ((InterruptedException e)){}
        }

        scrittura = true;

    }

    public synchronized void fineScrittura() {
        scrittura = false;
        notifyAll();
    }

}

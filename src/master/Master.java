public class Master{

    private int lettoriAttivi;
    private boolean inLettura;
    private boolean inScrittura;

    public Master() {
         lettoriAttivi = 0;
         inLettura = false;
         inScrittura = false;
    }
    public synchronized int inizioLettura() {
        while (inScrittura == true) {
            try {
                wait();
            }
            catch (InterruptedException e) {}
        }
        lettoriAttivi++;

        if (lettoriAttivi == 1)
            inLettura = true;

        return lettoriAttivi;
    }

    public synchronized int fineLettura() {
        lettoriAttivi--;
        if (lettoriAttivi == 0)
            notifyAll();
        return lettoriAttivi;
    }

    public synchronized void inizioScrittura() {
        while (inLettura == true || inScrittura == true){
            try {
                wait();
            }
            catch (InterruptedException e){}
        }
        inScrittura = true;
    }


    public synchronized void fineScrittura() {
        inScrittura = false;
        notifyAll();
    }
}

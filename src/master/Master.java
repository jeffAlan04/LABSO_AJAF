public class Database {
    public Database() {
        readerCount = 0;
        dbReading = false;
        dbWriting = false;
    }
    public synchronized int startRead() {
        while (dbWriting == true) {
            try {
                wait();
            }
            catch (InterruptedException e) {}
        }
        ++readerCount;

        if (readerCount == 1)
            dbReading = true;

        return readerCount;
    }

    public synchronized int endRead() {
        --readerCount;
        if (readerCount == 0)
            db.notifyAll();  //è sufficiente una notify() perchè nel wait-set sono presenti solo processi scrittori
        return readerCount;
    }

    public synchronized void startWrite() {
        while (dbReading == true || dbWriting == true){
            try {
                wait();
            }
            catch (InterruptedException e){}
        }
        dbWriting = true;
    }


    public synchronized void endWrite() {
        dbWriting = false;
        notifyAll();
    }

    private int readerCount;
    private boolean dbReading;
    private boolean dbWriting;
}

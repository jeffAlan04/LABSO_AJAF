import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ArbitroLetturaScrittura {

    // parametro true per avere implementazione con fairness fra lettori e scrittori
    private final ReentrantReadWriteLock semaforo = new ReentrantReadWriteLock(true);

    public void inizioLettura() {
        semaforo.readLock().lock();
    }

    public void fineLettura() {
        semaforo.readLock().unlock();
    }

    public void inizioScrittura() {
        semaforo.writeLock().lock();
    }

    public void fineScrittura() {
        semaforo.writeLock().unlock();
    }
}

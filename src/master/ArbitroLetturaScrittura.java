public class ArbitroLetturaScrittura {

    private int lettori; // numero di lettori che stanno accedendo
    private boolean lettura; // true se almeno un lettore ha l'accesso
    private boolean scrittura; // true se uno scrittore ha l'accesso

    public ArbitroLetturaScrittura() {
        lettori = 0;
        lettura = false;
        scrittura = false;
    }

}

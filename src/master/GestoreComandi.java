package master;

import java.io.*;

public class GestoreComandi implements Runnable{
    private final ArbitroLetturaScrittura arbitroLog;
    private final ArbitroLetturaScrittura arbitroTabella;

    public GestoreComandi(ArbitroLetturaScrittura arbitroLog, ArbitroLetturaScrittura arbitroTabella) {
        this.arbitroLog = arbitroLog;
        this.arbitroTabella = arbitroTabella;
    }
}

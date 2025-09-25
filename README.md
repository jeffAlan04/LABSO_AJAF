# LABSO_AJAF

## Dipendenze

Per gestire il salvataggio delle informazioni relative alla distribuzione delle risorse sulla rete, il progetto utilizza il formato JSON.
È necessario includere tre file JAR specifici per garantire il corretto funzionamento:

- jackson-annotations 2.17.0
- jackson-core 2.17.0
- jackson-databind 2.17.0

Posizionare i file JAR all'interno della cartella `lib/`.
Abbiamo testato il progetto con le versioni 2.17.0, ma anche le successive dovrebbero essere compatibili.

## Compilazione

Per compilare dentro `bin/` tutti i sorgenti dentro `src/`

```bash
make compile
```

Per rimuovere tutti i compilati da `bin`

```bash
make clean
```

## Esecuzione

Per eseguire `client`

```bash
make client MASTER_IP=[ip master] PORT=[porta]
```

Per eseguire `master`

```bash
make master PORT=[porta]
```

Se non speficicati, i valori di default definiti da `Makefile` sono:

- MASTER_IP = localhost
- PORT = 7000

# LABSO_AJAF

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

package master;

import java.util.*;

public class TabellaRisorse {
    private final Map<String, Set<String>> risorse = new HashMap<>();
    private final MasterSincro master = new MasterSincro();

    // Aggiunge una risorsa ad un peer
    public void aggiungiRisorsa(String risorsa, String indirizzoPeer) {
        master.inizioScrittura();
        try {
            risorse.putIfAbsent(risorsa, new HashSet<>());
            risorse.get(risorsa).add(indirizzoPeer);
        } finally {
            master.fineScrittura();
        }
    }

    // Rimuove una risorsa da un peer
    public void rimuoviRisorsa(String risorsa, String indirizzoPeer) {
        master.inizioScrittura();
        try {
            if (risorse.containsKey(risorsa)) {
                risorse.get(risorsa).remove(indirizzoPeer);
                if (risorse.get(risorsa).isEmpty()) {
                    risorse.remove(risorsa);
                }
            }
        } finally {
            master.fineScrittura();
        }
    }

    // Restituisce i peer che hanno una risorsa
    public Set<String> ottieniPeerPerRisorsa(String risorsa) {
        master.inizioLettura();
        try {
            return new HashSet<>(risorse.getOrDefault(risorsa, Collections.emptySet()));
        } finally {
            master.fineLettura();
        }
    }

    // Restituisce tutte le risorse
    public Map<String, Set<String>> ottieniTutteRisorse() {
        master.inizioLettura();
        try {
            return new HashMap<>(risorse);
        } finally {
            master.fineLettura();
        }
    }
}

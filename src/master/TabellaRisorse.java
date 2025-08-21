package master;

import java.util.*;
import java.util.concurrent.*;

// Classe che gestisce le risorse condivise dai peer
public class TabellaRisorse {
    // Map: nome risorsa -> set di peer che la possiedono
    private final Map<String, Set<String>> risorse = new ConcurrentHashMap<>();

    // Map: nome peer -> stato attivo (true se connesso)
    private final Map<String, Boolean> peerAttivi = new ConcurrentHashMap<>();

    /** Registra un peer come attivo */
    public void registraPeer(String nomePeer) {
        peerAttivi.put(nomePeer, true);
    }

    /** Disconnetti un peer e rimuovilo dalle risorse */
    public void disconnettiPeer(String nomePeer) {
        peerAttivi.put(nomePeer, false);
        risorse.forEach((risorsa, peers) -> peers.remove(nomePeer));
    }

    /** Controlla se un peer è attivo */
    public boolean isAttivo(String nomePeer) {
        return peerAttivi.getOrDefault(nomePeer, false);
    }

    /** Aggiunge una risorsa associandola a un peer */
    public void aggiungiRisorsa(String risorsa, String nomePeer) {
        risorse.computeIfAbsent(risorsa, k -> ConcurrentHashMap.newKeySet())
                .add(nomePeer);
    }

    /** Restituisce tutti i peer che possiedono una risorsa */
    public Set<String> ottieniPeerPerRisorsa(String risorsa) {
        return risorse.getOrDefault(risorsa, Collections.emptySet());
    }

    /** Restituisce una copia di tutte le risorse e dei rispettivi peer */
    public Map<String, Set<String>> ottieniTutteRisorse() {
        Map<String, Set<String>> copia = new HashMap<>();
        risorse.forEach((risorsa, peers) -> copia.put(risorsa, new HashSet<>(peers)));
        return copia;
    }
}

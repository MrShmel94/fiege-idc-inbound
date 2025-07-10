package idc.inbound.configuration;


import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WebSocketSubscriptionRegistry {

    private final ConcurrentHashMap<String, Set<String>> subscriptions = new ConcurrentHashMap<>();

    public void subscribe(String topic, String sessionId) {
        subscriptions.computeIfAbsent(topic, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
    }

    public void unsubscribe(String topic, String sessionId) {
        Set<String> sessions = subscriptions.get(topic);
        if (sessions != null) {
            sessions.remove(sessionId);
            if (sessions.isEmpty()) {
                subscriptions.remove(topic);
            }
        }
    }

    public Set<String> getSubscribers(String topic) {
        return subscriptions.getOrDefault(topic, Set.of());
    }

    public Set<String> getActiveTopics() {
        return subscriptions.keySet();
    }
}

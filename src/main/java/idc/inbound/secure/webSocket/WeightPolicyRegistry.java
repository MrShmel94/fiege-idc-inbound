package idc.inbound.secure.webSocket;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class WeightPolicyRegistry {

    private final WeightPolicyProperties props;

    public Integer getMinWeightFor(String requestPath) {
        Integer best = null;
        int bestLen = -1;
        for (Map.Entry<String, Integer> e : props.getPolicies().entrySet()) {
            String path = e.getKey().startsWith("/")
                    ? e.getKey()
                    : "/" + e.getKey();

            if (requestPath.startsWith(path) && path.length() > bestLen) {
                best = e.getValue();
                bestLen = path.length();
            }
        }
        return best;
    }
}

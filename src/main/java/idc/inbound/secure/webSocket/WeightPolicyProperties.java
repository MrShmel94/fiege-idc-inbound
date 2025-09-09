package idc.inbound.secure.webSocket;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix = "ws")
public class WeightPolicyProperties {
    /**
     * endpointPath -> minWeight
     */
    private Map<String, Integer> policies = new HashMap<>();
}

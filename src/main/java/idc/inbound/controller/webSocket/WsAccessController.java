package idc.inbound.controller.webSocket;

import idc.inbound.secure.webSocket.WeightPolicyRegistry;
import idc.inbound.secure.CustomUserDetails;
import idc.inbound.service.vision.UserService;
import idc.inbound.utils.Utils;
import idc.inbound.secure.SecurityConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ws")
public class WsAccessController {

    private final WeightPolicyRegistry registry;
    private final UserService userService;
    private final Utils utils;
    private final Environment env;

    @GetMapping("/permit")
    public ResponseEntity<?> canConnect(@RequestParam("endpoint") String endpoint,
                                        HttpServletRequest request) {
        String ep = endpoint.startsWith("/") ? endpoint : "/" + endpoint;

        Integer minWeight = registry.getMinWeightFor(ep);
        if (minWeight == null) {
            return ResponseEntity.status(403).body("ENDPOINT_NOT_ALLOWED");
        }

        String token = resolveToken(request);
        if (token == null) {
            return ResponseEntity.status(403).body("NO_TOKEN");
        }

        var claims = utils.parseToken(token);
        if (claims == null) {
            return ResponseEntity.status(403).body("BAD_TOKEN");
        }

        String userId = claims.getSubject();
        CustomUserDetails user = (CustomUserDetails) userService.loadUserByUsernameWithoutPassword(userId);
        int weight = user.role().getWeight();

        if (weight < minWeight) {
            return ResponseEntity.status(403).body("INSUFFICIENT_WEIGHT");
        }

        return ResponseEntity.noContent().build();
    }

    private String resolveToken(HttpServletRequest http) {
        if (http.getCookies() != null) {
            for (Cookie c : http.getCookies()) {
                if ("AccessToken".equals(c.getName())) return c.getValue();
            }
        }
        boolean prod = Arrays.asList(env.getActiveProfiles()).contains("prod");
        if (!prod) {
            String h = http.getHeader("Authorization");
            if (h != null && h.startsWith(SecurityConstants.TOKEN_PREFIX)) {
                return h.substring(SecurityConstants.TOKEN_PREFIX.length());
            }
        }
        return null;
    }
}

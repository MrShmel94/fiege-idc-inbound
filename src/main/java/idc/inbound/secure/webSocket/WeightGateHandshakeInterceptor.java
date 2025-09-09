package idc.inbound.secure.webSocket;

import idc.inbound.secure.CustomUserDetails;
import idc.inbound.secure.SecurityConstants;
import idc.inbound.service.vision.UserService;
import idc.inbound.utils.Utils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Arrays;
import java.util.Map;

@RequiredArgsConstructor
public class WeightGateHandshakeInterceptor implements HandshakeInterceptor {

    private final Utils utils;
    private final UserService userService;
    private final WeightPolicyRegistry registry;
    private final Environment env;

    @Override
    public boolean beforeHandshake(@NotNull ServerHttpRequest request,
                                   @NotNull ServerHttpResponse response,
                                   @NotNull WebSocketHandler wsHandler,
                                   @NotNull Map<String, Object> attributes) {
        String path = (request instanceof ServletServerHttpRequest s)
                ? s.getServletRequest().getRequestURI()
                : request.getURI().getPath();

        Integer minWeight = registry.getMinWeightFor(path);
        if (minWeight == null) {
            set403(response);
            return false;
        }

        String token = resolveToken(request);
        if (token == null) {
            set403(response);
            return false;
        }

        try {
            Claims claims = utils.parseToken(token);
            if (claims == null) {
                set403(response);
                return false;
            }
            String userId = claims.getSubject();
            CustomUserDetails user = (CustomUserDetails) userService.loadUserByUsernameWithoutPassword(userId);

            int weight = user.role().getWeight();
            if (weight < minWeight) {
                set403(response);
                return false;
            }

            Authentication auth =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

            attributes.put("AccessToken", token);
            attributes.put("EndpointPath", path);
            attributes.put("UserWeight", weight);
            return true;
        } catch (Exception ex) {
            set403(response);
            return false;
        }
    }

    @Override
    public void afterHandshake(@NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response,
                               @NotNull WebSocketHandler wsHandler, Exception exception) {}

    private void set403(ServerHttpResponse response) {
        try { response.setStatusCode(HttpStatus.FORBIDDEN); } catch (Exception ignored) {}
    }

    private String resolveToken(ServerHttpRequest request) {
        boolean prod = Arrays.asList(env.getActiveProfiles()).contains("prod");

        if (request instanceof ServletServerHttpRequest s) {
            HttpServletRequest http = s.getServletRequest();

            if (http.getCookies() != null) {
                for (Cookie c : http.getCookies()) {
                    if ("AccessToken".equals(c.getName())) return c.getValue();
                }
            }

            if (!prod) {
                String h = http.getHeader("Authorization");
                if (h != null && h.startsWith(SecurityConstants.TOKEN_PREFIX)) {
                    return h.substring(SecurityConstants.TOKEN_PREFIX.length());
                }
            }
        }
        return null;
    }
}

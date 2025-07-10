package idc.inbound.secure.webSocket;

import idc.inbound.secure.CustomUserDetails;
import idc.inbound.secure.SecurityConstants;
import idc.inbound.service.vision.UserService;
import idc.inbound.utils.Utils;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    private final Utils utils;
    private final UserService userService;


    @Override
    public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = null;
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader == null) {
                Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
                if (sessionAttributes != null && sessionAttributes.get("AccessToken") != null) {
                    token = sessionAttributes.get("AccessToken").toString();
                }
                if (token == null) {
                    return message;
                }
            } else if (authHeader.startsWith(SecurityConstants.TOKEN_PREFIX)) {
                token = authHeader.substring(SecurityConstants.TOKEN_PREFIX.length());
            }
            try {
                Claims claims = utils.parseToken(token);
                if (claims != null) {
                    String userId = claims.getSubject();
                    CustomUserDetails userDetails = (CustomUserDetails) userService.loadUserByUsernameWithoutPassword(userId);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    accessor.setUser(authentication);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid or expired JWT token", e);
            }
        }
        return message;
    }
}

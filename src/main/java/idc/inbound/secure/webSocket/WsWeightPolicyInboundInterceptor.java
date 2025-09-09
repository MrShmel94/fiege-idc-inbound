package idc.inbound.secure.webSocket;


import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class WsWeightPolicyInboundInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) return message;

        StompCommand cmd = accessor.getCommand();
        if (cmd == null) {
            SimpMessageType type = accessor.getMessageType();
            if (type == SimpMessageType.HEARTBEAT) return message;
        }

        Map<String, Object> attrs = accessor.getSessionAttributes();
        if (attrs == null) return message;

        Integer userWeight = (Integer) attrs.get("UserWeight");
        Integer minWeight = null;
        String endpoint = (String) attrs.get("EndpointPath");

        if (userWeight == null || endpoint == null) return message;

        Object mw = attrs.get("MinWeight");
        if (mw instanceof Integer) {
            minWeight = (Integer) mw;
        }

        if (minWeight != null && userWeight < minWeight) {
            throw new AccessDeniedException("Forbidden by weight policy");
        }

        return message;
    }
}

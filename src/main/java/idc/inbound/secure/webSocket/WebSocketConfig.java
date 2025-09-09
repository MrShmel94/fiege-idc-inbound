package idc.inbound.secure.webSocket;

import idc.inbound.service.vision.UserService;
import idc.inbound.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@EnableConfigurationProperties(WeightPolicyProperties.class)
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthChannelInterceptor webSocketAuthChannelInterceptor;

    private final WsWeightPolicyInboundInterceptor wsWeightPolicyInboundInterceptor;

    private final Utils utils;
    private final UserService userService;
    private final WeightPolicyRegistry weightPolicyRegistry;
    private final Environment env;

    @Bean
    public WeightGateHandshakeInterceptor weightGateHandshakeInterceptor() {
        return new WeightGateHandshakeInterceptor(utils, userService, weightPolicyRegistry, env);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        var authCookie = new AuthHandshakeInterceptor();
        var weightGate = weightGateHandshakeInterceptor();

        registry.addEndpoint("/unloading-report")
                .setAllowedOrigins("https://localhost:3000", "https://fiege-vision.com", "https://www.fiege-vision.com")
                .addInterceptors(authCookie, weightGate)
                .withSockJS();

        registry.addEndpoint("/forklift-socket")
                .setAllowedOrigins("https://localhost:3000", "https://fiege-vision.com", "https://www.fiege-vision.com")
                .addInterceptors(authCookie, weightGate)
                .withSockJS();

        registry.addEndpoint("/config")
                .setAllowedOrigins("https://localhost:3000", "https://fiege-vision.com", "https://www.fiege-vision.com")
                .addInterceptors(authCookie, weightGate)
                .withSockJS();

        registry.addEndpoint("/yard-man")
                .setAllowedOrigins("https://localhost:3000", "https://fiege-vision.com", "https://www.fiege-vision.com")
                .addInterceptors(authCookie, weightGate)
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration
                .interceptors(webSocketAuthChannelInterceptor)
                .interceptors(wsWeightPolicyInboundInterceptor);
    }
}
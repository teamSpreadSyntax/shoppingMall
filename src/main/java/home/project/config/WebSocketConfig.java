package home.project.config;

import home.project.service.websocket.WebSocketAuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
//@ConditionalOnProperty(name = "websocket.enabled", havingValue = "true", matchIfMissing = true)//웹소켓 비활성화
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthInterceptor webSocketAuthInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");// -> /topic: 일대다(1:N) 메시징, 브로드캐스팅에 사용, /queue: 일대일(1:1) 메시징, 특정 사용자 간 통신에 사용
        config.setApplicationDestinationPrefixes("/app");//클라이언트가 서버로 메시지를 보낼 때 사용할 prefix, 예: 클라이언트가 /app/something으로 메시지를 보내면 @MessageMapping("/something")이 처리
        config.setUserDestinationPrefix("/user");  // 특정 사용자에게 메시지를 보낼 때 사용하는 prefix, 내부적으로 /user/{userId}/queue/... 형태로 변환, convertAndSendToUser() 메서드 사용 시 이 prefix가 자동으로 붙음
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketAuthInterceptor);  // 인증 인터셉터 추가
    }
}

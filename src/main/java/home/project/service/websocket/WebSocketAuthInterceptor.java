package home.project.service.websocket;

import home.project.service.security.JwtTokenProvider;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class WebSocketAuthInterceptor implements ChannelInterceptor {
    private final JwtTokenProvider jwtTokenProvider;  // 기존 JWT 관련 클래스 사용

    public WebSocketAuthInterceptor(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // 연결 시도시 토큰 검증
            String authToken = accessor.getFirstNativeHeader("Authorization");
            if (authToken != null && authToken.startsWith("Bearer ")) {
                String token = authToken.substring(7);
                if (jwtTokenProvider.validateToken(token)) {
                    // 토큰에서 사용자 정보 추출
                    String email = jwtTokenProvider.getEmailFromToken(token);

                    // Spring Security의 인증 객체 생성
                    Authentication auth = jwtTokenProvider.getAuthentication(token);
                    accessor.setUser(auth);  // WebSocket 세션에 인증 정보 설정
                }
            }
        }
        return message;
    }
}

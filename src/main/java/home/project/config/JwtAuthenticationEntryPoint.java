package home.project.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import home.project.response.CustomOptionalResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("errorMessage", "인증 토큰이 필요합니다.(로그인 해주세요.)");
        CustomOptionalResponseBody<Map<String, Object>> errorBody = new CustomOptionalResponseBody<>(Optional.of(responseBody), "인증되지 않은 사용자입니다.", HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(objectMapper.writeValueAsString(errorBody));
    }
}
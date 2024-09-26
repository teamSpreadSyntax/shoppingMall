package home.project.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import home.project.response.CustomResponseBody;
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
        responseBody.put("errorMessage", "토큰이 유효하지 않습니다.(로그인 또는 본인확인을 다시 진행해주세요.)");
        CustomResponseBody<Map<String, Object>> errorBody = new CustomResponseBody<>(Optional.of(responseBody), "인증되지 않은 사용자입니다.", HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(objectMapper.writeValueAsString(errorBody));
    }
}
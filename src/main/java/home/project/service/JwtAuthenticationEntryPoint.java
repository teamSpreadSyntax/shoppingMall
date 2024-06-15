package home.project.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import home.project.domain.CustomOptionalResponseBody;
import home.project.domain.CustomOptionalResponseEntity;
import home.project.domain.Member;
import home.project.domain.Product;
import home.project.exceptions.GlobalExceptionHandler;
import home.project.exceptions.JwtAuthenticationException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    // 토큰이 없는 사용자의 요청을 처리
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("errorMessage", "인증 토큰이 필요합니다");

        CustomOptionalResponseBody<Map<String, Object>> errorBody =
                new CustomOptionalResponseBody<>(Optional.of(responseBody), "인증되지 않은 사용자입니다", HttpStatus.UNAUTHORIZED.value());

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(objectMapper.writeValueAsString(errorBody));
    }
}
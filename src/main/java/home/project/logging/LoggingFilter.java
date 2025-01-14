package home.project.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoggingFilter implements Filter {
    private final ObjectMapper objectMapper;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // HTTP 요청이 아닌 경우 처리하지 않음
        if (!(request instanceof HttpServletRequest httpRequest) || !(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(httpRequest);
        ContentCachingResponseWrapper responseWrapper =
                new ContentCachingResponseWrapper((HttpServletResponse) response);

        // 요청 추적을 위한 고유 ID 생성
        String traceId = UUID.randomUUID().toString();
        MDC.put("traceId", traceId);

        // 시작 시간 기록
        long startTime = System.currentTimeMillis();

        try {
            // 실제 요청 처리
            chain.doFilter(requestWrapper, responseWrapper);
        } finally {
            // 요청 처리 시간 계산
            long duration = System.currentTimeMillis() - startTime;

            // 로그 정보 생성
            LogFormat logInfo = LogFormat.builder()
                    .timestamp(LocalDateTime.now())
                    .traceId(traceId)
                    .requestUri(httpRequest.getRequestURI())
                    .method(httpRequest.getMethod())
                    .params(getRequestBody(requestWrapper))
                    .response(getResponseBody(responseWrapper))
                    .elapsedTime(duration)
                    .clientIp(getClientIp(httpRequest))
                    .userAgent(httpRequest.getHeader("User-Agent"))
                    .sessionId(httpRequest.getSession(false) != null ?
                            httpRequest.getSession().getId() : null)
                    .build();

            // 처리 시간에 따른 로그 레벨 조정
            if (duration > 3000) {  // 3초 이상 걸린 경우
                log.warn("[SLOW-API] {}", objectMapper.writeValueAsString(logInfo));
            } else if (duration > 1000) {  // 1초 이상 걸린 경우
                log.warn("[DELAYED-API] {}", objectMapper.writeValueAsString(logInfo));
            } else {
                log.info("[API-LOG] {}", objectMapper.writeValueAsString(logInfo));
            }

            // 리소스 정리
            MDC.clear();
            responseWrapper.copyBodyToResponse();
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String[] headers = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED",
                "HTTP_VIA",
                "REMOTE_ADDR"
        };

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // 쉼표로 구분된 IP 중 첫 번째 IP 반환
                return ip.split(",")[0].trim();
            }
        }

        return request.getRemoteAddr();
    }

    private Object getRequestBody(ContentCachingRequestWrapper request) {
        try {
            String body = new String(request.getContentAsByteArray());
            if (!body.isEmpty()) {
                // 민감 정보 마스킹 처리
                return maskSensitiveData(objectMapper.readValue(body, Object.class));
            }
            return null;
        } catch (IOException e) {
            log.warn("Failed to read request body", e);
            return null;
        }
    }

    private Object getResponseBody(ContentCachingResponseWrapper response) {
        try {
            String body = new String(response.getContentAsByteArray());
            if (!body.isEmpty()) {
                return objectMapper.readValue(body, Object.class);
            }
            return null;
        } catch (IOException e) {
            log.warn("Failed to read response body", e);
            return null;
        }
    }

    private Object maskSensitiveData(Object data) {
        if (data instanceof java.util.Map) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> map = (java.util.Map<String, Object>) data;
            java.util.Map<String, Object> maskedMap = new java.util.HashMap<>(map);

            // 민감 정보 필드 마스킹
            String[] sensitiveFields = {"password", "token", "credential", "cardNumber", "ssn"};
            for (String field : sensitiveFields) {
                if (maskedMap.containsKey(field)) {
                    maskedMap.put(field, "********");
                }
            }

            return maskedMap;
        }
        return data;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 필터 초기화 시 필요한 작업이 있다면 여기에 구현
    }

    @Override
    public void destroy() {
        // 필터 종료 시 필요한 작업이 있다면 여기에 구현
    }
}
package home.project.logging;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class LogFormat {
    private LocalDateTime timestamp;
    private String traceId;
    private String requestUri;
    private String method;
    private String className;
    private String methodName;
    private Object params;
    private Object response;
    private String errorMessage;
    private long elapsedTime;
    private String clientIp;
    private String userAgent;
    private String sessionId;
}
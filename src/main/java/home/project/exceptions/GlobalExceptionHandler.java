package home.project.exceptions;

import home.project.domain.CustomOptionalResponseBody;
import home.project.domain.CustomOptionalResponseEntity;
import home.project.domain.Product;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ControllerAdvice

public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(PageNotFoundException.class)
    public ResponseEntity<?> handlePageNotFoundException(PageNotFoundException e) {
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("errorMessage", e.getMessage());
        CustomOptionalResponseBody<?> errorBody = new CustomOptionalResponseBody<>(Optional.ofNullable(responseBody), "해당 페이지가 존재하지 않습니다", HttpStatus.BAD_REQUEST.value());
        return new CustomOptionalResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("errorMessage", e.getMessage());
        CustomOptionalResponseBody<Optional<Product>> errorBody = new CustomOptionalResponseBody<>(Optional.ofNullable(responseMap), "검색내용이 존재하지 않습니다.", HttpStatus.NOT_FOUND.value());
        return new CustomOptionalResponseEntity<>(errorBody, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("errorMessage", e.getMessage());
        CustomOptionalResponseBody<Optional<Product>> errorBody = new CustomOptionalResponseBody<>(Optional.ofNullable(responseMap), "데이터 무결성 위반 오류입니다.", HttpStatus.CONFLICT.value());
        return new CustomOptionalResponseEntity<>(errorBody, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("errorMessage", ex.getMessage());
        CustomOptionalResponseBody<?> errorBody = new CustomOptionalResponseBody<>(Optional.ofNullable(responseBody), "아이디가 존재하지 않습니다", HttpStatus.UNAUTHORIZED.value());
        return new CustomOptionalResponseEntity<>(errorBody, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("errorMessage", ex.getMessage());
        CustomOptionalResponseBody<?> errorBody = new CustomOptionalResponseBody<>(Optional.ofNullable(responseBody), "인증되지 않은 사용자입니다", HttpStatus.UNAUTHORIZED.value());
        return new CustomOptionalResponseEntity<>(errorBody, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<?> handleJwtAuthenticationException(JwtAuthenticationException ex) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("errorMessage", ex.getMessage());
        CustomOptionalResponseBody<?> errorBody = new CustomOptionalResponseBody<>(Optional.ofNullable(responseBody), "인증되지 않은 사용자입니다", HttpStatus.UNAUTHORIZED.value());
        return new CustomOptionalResponseEntity<>(errorBody, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException ex) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("errorMessage", ex.getMessage());
        CustomOptionalResponseBody<?> errorBody = new CustomOptionalResponseBody<>(Optional.ofNullable(responseBody), "비밀번호가 틀립니다", HttpStatus.UNAUTHORIZED.value());
        return new CustomOptionalResponseEntity<>(errorBody, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("errorMessage", e.getMessage());
        CustomOptionalResponseBody<Optional<Product>> errorBody = new CustomOptionalResponseBody<>(Optional.ofNullable(responseMap), "서버 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new CustomOptionalResponseEntity<>(errorBody, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}



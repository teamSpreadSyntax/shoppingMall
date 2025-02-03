package home.project.exceptions;

import home.project.exceptions.exception.InsufficientPointsException;
import home.project.exceptions.exception.*;
import home.project.response.CustomResponseBody;
import home.project.response.CustomResponseEntity;
import io.jsonwebtoken.JwtException;
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
        CustomResponseBody<?> errorBody = new CustomResponseBody<>(Optional.of(responseBody), "해당 페이지가 존재하지 않습니다.", HttpStatus.BAD_REQUEST.value());
        return new CustomResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("errorMessage", e.getMessage());
        CustomResponseBody<Map<String, String>> errorBody = new CustomResponseBody<>(Optional.of(responseMap), "검색내용이 존재하지 않습니다.", HttpStatus.BAD_REQUEST.value());

        return new CustomResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalStateException(IllegalStateException e) {
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("errorMessage", e.getMessage());
        CustomResponseBody<Map<String, String>> errorBody = new CustomResponseBody<>(Optional.of(responseMap), "입력값을 확인해주세요.", HttpStatus.BAD_REQUEST.value());

        return new CustomResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(IdNotFoundException.class)
    public ResponseEntity<?> handleIdNotFoundException(IdNotFoundException e) {
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("errorMessage", e.getMessage());
        CustomResponseBody<Map<String, String>> errorBody = new CustomResponseBody<>(Optional.of(responseMap), "검색내용이 존재하지 않습니다.", HttpStatus.NOT_FOUND.value());

        return new CustomResponseEntity<>(errorBody, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("errorMessage", e.getMessage());
        CustomResponseBody<Map<String, String>> errorBody = new CustomResponseBody<>(Optional.of(responseMap), "데이터 무결성 위반 오류입니다.", HttpStatus.CONFLICT.value());
        return new CustomResponseEntity<>(errorBody, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("errorMessage", ex.getMessage());
        CustomResponseBody<?> errorBody = new CustomResponseBody<>(Optional.of(responseBody), "해당 회원이 존재하지 않습니다.", HttpStatus.UNAUTHORIZED.value());
        return new CustomResponseEntity<>(errorBody, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("errorMessage", ex.getMessage());
        CustomResponseBody<?> errorBody = new CustomResponseBody<>(Optional.of(responseBody), "인증되지 않은 사용자입니다.", HttpStatus.FORBIDDEN.value());
        return new CustomResponseEntity<>(errorBody, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InvalidCouponException.class)
    public ResponseEntity<?> handleInvalidCouponException(InvalidCouponException ex) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("errorMessage", ex.getMessage());
        CustomResponseBody<?> errorBody = new CustomResponseBody<>(Optional.of(responseBody), "인증되지 않은 사용자입니다.", HttpStatus.FORBIDDEN.value());
        return new CustomResponseEntity<>(errorBody, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<?> handleJwtAuthenticationException(JwtAuthenticationException ex) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("errorMessage", ex.getMessage());
        CustomResponseBody<?> errorBody = new CustomResponseBody<>(Optional.of(responseBody), "인증되지 않은 사용자입니다.", HttpStatus.UNAUTHORIZED.value());
        return new CustomResponseEntity<>(errorBody, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<?> handleJwtException(JwtException e) {
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("errorMessage", e.getMessage());
        CustomResponseBody<Map<String, String>> errorBody = new CustomResponseBody<>(Optional.of(responseBody), "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED.value());
        return new CustomResponseEntity<>(errorBody, HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException ex) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("errorMessage", ex.getMessage());
        CustomResponseBody<?> errorBody = new CustomResponseBody<>(Optional.of(responseBody), "비밀번호가 틀립니다.", HttpStatus.UNAUTHORIZED.value());
        return new CustomResponseEntity<>(errorBody, HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("errorMessage", e.getMessage());
        CustomResponseBody<?> errorBody = new CustomResponseBody<>(Optional.of(responseBody), "서버 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new CustomResponseEntity<>(errorBody, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoChangeException.class)
    public ResponseEntity<?> noChangeException(NoChangeException e) {
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("errorMessage", e.getMessage());
        CustomResponseBody<?> errorBody = new CustomResponseBody<>(Optional.of(responseBody), "변경된 정보가 없습니다", HttpStatus.NO_CONTENT.value());
        return new CustomResponseEntity<>(errorBody, HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(AddressSearchException.class)
    public ResponseEntity<?> handleAddressSearchException(AddressSearchException e) {
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("errorMessage", e.getMessage());
        CustomResponseBody<Map<String, String>> errorBody = new CustomResponseBody<>(
                Optional.of(responseMap),
                "주소 검색 중 오류가 발생했습니다.",
                HttpStatus.BAD_REQUEST.value()
        );
        return new CustomResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AddressNotFoundException.class)
    public ResponseEntity<?> handleAddressNotFoundException(AddressNotFoundException e) {
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("errorMessage", e.getMessage());
        CustomResponseBody<Map<String, String>> errorBody = new CustomResponseBody<>(
                Optional.of(responseMap),
                "검색 결과가 없습니다.",
                HttpStatus.NOT_FOUND.value()
        );
        return new CustomResponseEntity<>(errorBody, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(InsufficientPointsException.class)
    public ResponseEntity<?> handleInsufficientPointsException(InsufficientPointsException e) {
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("errorMessage", e.getMessage());

        CustomResponseBody<?> errorBody = new CustomResponseBody<>(
                Optional.of(responseBody),
                "보유 포인트가 부족합니다.",
                HttpStatus.BAD_REQUEST.value()
        );

        return new CustomResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
    }
}



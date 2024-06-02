package home.project.domain;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class CustomPageResponseEntity<T> extends ResponseEntity<CustomPageResponseBody<T>> {
    public CustomPageResponseEntity(CustomPageResponseBody<T> body, HttpStatus status) {
        super(body, new HttpHeaders(), status);
    }

    public CustomPageResponseEntity(List<T> listData, String message, HttpStatus status, long totalCount, int page) {
        super(new CustomPageResponseBody<>(new CustomPageResponseBody.Result<>(totalCount, page, listData), message, status.value()), new HttpHeaders(), status);
    }
}
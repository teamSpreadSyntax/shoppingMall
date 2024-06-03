package home.project.domain;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public class CustomOptionalPageResponseEntity<T> extends ResponseEntity<CustomOptionalPageResponseBody<T>> {
    public CustomOptionalPageResponseEntity(CustomOptionalPageResponseBody<T> body, HttpStatus status) {
        super(body, new HttpHeaders(), status);
    }
    public CustomOptionalPageResponseEntity(Optional<List<T>> content, String responseMessage, HttpStatus status, long totalCount, int page) {
        super(new CustomOptionalPageResponseBody<>(new CustomOptionalPageResponseBody.Result<>(totalCount, page, content), responseMessage, status.value()), new HttpHeaders(), status);
    }
}

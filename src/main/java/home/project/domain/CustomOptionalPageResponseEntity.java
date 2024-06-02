package home.project.domain;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class CustomOptionalPageResponseEntity<T> extends ResponseEntity<CustomOptionalPageResponseBody<T>> {
    public CustomOptionalPageResponseEntity(CustomOptionalPageResponseBody<T> body, HttpStatus status) {
        super(body, new HttpHeaders(), status);
    }
}

package home.project.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

@Getter
@Setter
public class CustomOptionalPageResponseEntity<T> extends ResponseEntity<CustomOptionalPageResponseBody<T>> {

    public CustomOptionalPageResponseEntity(CustomOptionalPageResponseBody<T> body, HttpStatus status) {
        super(body, new HttpHeaders(), status);
    }

    public CustomOptionalPageResponseEntity(Optional<T> OptionalData, String message, HttpStatus status,long total) {
        super(new CustomOptionalPageResponseBody<>(OptionalData, message, total), new HttpHeaders(), status);
    }
}
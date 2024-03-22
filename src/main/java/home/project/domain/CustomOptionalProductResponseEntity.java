package home.project.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

@Getter
@Setter
public class CustomOptionalProductResponseEntity<T> extends ResponseEntity<CustomOptionalProductResponseBody<T>> {

    public CustomOptionalProductResponseEntity(CustomOptionalProductResponseBody<T> body, HttpStatus status) {
        super(body, new HttpHeaders(), status);
    }

    public CustomOptionalProductResponseEntity(Optional<T> OptionalData, String message, HttpStatus status) {
        super(new CustomOptionalProductResponseBody<>(OptionalData, message), new HttpHeaders(), status);
    }
}
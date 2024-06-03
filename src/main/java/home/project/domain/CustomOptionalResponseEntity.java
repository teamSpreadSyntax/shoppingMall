package home.project.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

@Getter
@Setter
public class CustomOptionalResponseEntity<T> extends ResponseEntity<CustomOptionalResponseBody<T>> {

    public CustomOptionalResponseEntity(CustomOptionalResponseBody<T> body, HttpStatus status) {
        super(body, new HttpHeaders(), status);
    }

    public CustomOptionalResponseEntity(Optional<T> OptionalData, String responseMessage, HttpStatus status) {
        super(new CustomOptionalResponseBody<>(OptionalData, responseMessage), new HttpHeaders(), status);
    }
}
package home.project.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

@Getter
@Setter
@Schema(name = "CustomOptionalResponseEntity", description = "Custom response entity for optional data")
public class CustomOptionalResponseEntity<T> extends ResponseEntity<CustomOptionalResponseBody<T>> {

    public CustomOptionalResponseEntity(CustomOptionalResponseBody<T> body, HttpStatus status) {
        super(body, new HttpHeaders(), status);
    }

    public CustomOptionalResponseEntity(Optional<T> content, String responseMessage, HttpStatus status) {
        super(new CustomOptionalResponseBody<>(content, responseMessage, status.value()), new HttpHeaders(), status);
    }
}
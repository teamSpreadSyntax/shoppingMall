package home.project.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

@Getter
@Setter
public class CustomOptionalMemberResponseEntity<T> extends ResponseEntity<CustomOptionalMemberResponseBody<T>> {

    public CustomOptionalMemberResponseEntity(CustomOptionalMemberResponseBody<T> body, HttpStatus status) {
        super(body, new HttpHeaders(), status);
    }

    public CustomOptionalMemberResponseEntity(Optional<T> OptionalData, String message, HttpStatus status) {
        super(new CustomOptionalMemberResponseBody<>(OptionalData, message), new HttpHeaders(), status);
    }
}
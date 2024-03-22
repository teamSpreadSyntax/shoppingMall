package home.project.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Getter
@Setter
public class CustomListProductResponseEntity<T> extends ResponseEntity<CustomListProductResponseBody<T>>{
    public CustomListProductResponseEntity(CustomListProductResponseBody<T> body, HttpStatus status) {
        super(body, new HttpHeaders(), status);
    }

    public CustomListProductResponseEntity(List<T> listData, String message, HttpStatus status) {
        super(new CustomListProductResponseBody<>(listData, message), new HttpHeaders(), status);
    }
}

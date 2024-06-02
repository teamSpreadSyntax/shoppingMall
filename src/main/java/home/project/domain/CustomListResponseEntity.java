package home.project.domain;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class CustomListResponseEntity<T> extends ResponseEntity<CustomListResponseBody<T>> {
    public CustomListResponseEntity(CustomListResponseBody<T> body, HttpStatus status) {
        super(body, new HttpHeaders(), status);
    }

    public CustomListResponseEntity(List<T> content, String responseMessage, HttpStatus status, long totalCount, int page) {
        super(new CustomListResponseBody<>(new CustomListResponseBody.Result<>(totalCount, page, content), responseMessage, status.value()), new HttpHeaders(), status);
    }
}

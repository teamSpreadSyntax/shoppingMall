package home.project.response;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public class CustomResponseEntity<T> extends ResponseEntity<CustomResponseBody<T>> {

    public CustomResponseEntity(List<T> content, String responseMessage, HttpStatus status, long totalCount, int page) {
        super(new CustomResponseBody<>(
                new CustomResponseBody.ListResult<>(totalCount, page, content),
                responseMessage,
                status.value()
        ), new HttpHeaders(), status);
    }

    public CustomResponseEntity(Optional<T> content, String responseMessage, HttpStatus status) {
        super(new CustomResponseBody<>(
                content,
                responseMessage,
                status.value()
        ), new HttpHeaders(), status);
    }

    public CustomResponseEntity(T content, String responseMessage, HttpStatus status) {
        super(new CustomResponseBody<>(
                Optional.ofNullable(content),
                responseMessage,
                status.value()
        ), new HttpHeaders(), status);
    }

    public CustomResponseEntity(CustomResponseBody<T> body, HttpStatus status) {
        super(body, new HttpHeaders(), status);
    }
}

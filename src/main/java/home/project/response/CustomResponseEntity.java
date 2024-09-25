package home.project.response;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public class CustomResponseEntity<T> extends ResponseEntity<CustomResponseBody.ResponseBody<T>> {

    public CustomResponseEntity(List<T> content, String responseMessage, HttpStatus status, long totalCount, int page) {
        super(new CustomResponseBody.ResponseBody<>(
                new CustomResponseBody.ListResult<>(totalCount, page, content),
                responseMessage,
                status.value()
        ), new HttpHeaders(), status);
    }

    public CustomResponseEntity(Optional<T> content, String responseMessage, HttpStatus status) {
        super(new CustomResponseBody.ResponseBody<>(
                content,
                responseMessage,
                status.value()
        ), new HttpHeaders(), status);
    }

    public CustomResponseEntity(T content, String responseMessage, HttpStatus status) {
        super(new CustomResponseBody.ResponseBody<>(
                Optional.ofNullable(content),
                responseMessage,
                status.value()
        ), new HttpHeaders(), status);
    }
}

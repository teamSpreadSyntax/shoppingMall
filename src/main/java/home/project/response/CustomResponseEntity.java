package home.project.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@JsonIgnoreProperties({"headers", "statusCode", "statusCodeValue"})
public class CustomResponseEntity<T> extends ResponseEntity<CustomResponseBody<T>> {

    public CustomResponseEntity(List<T> content, String responseMessage, HttpStatus status, long totalCount, int page) {
        super(new CustomResponseBody<>(
                new CustomResponseBody.ListResult<>(totalCount, page, content),
                responseMessage,
                status.value()
        ), new HttpHeaders(), status);
    }


    public CustomResponseEntity(T content, String responseMessage, HttpStatus status) {
        super(new CustomResponseBody<>(
                content,
                responseMessage,
                status.value()
        ), new HttpHeaders(), status);
    }

    public CustomResponseEntity(CustomResponseBody<T> body, HttpStatus status) {
        super(body, new HttpHeaders(), status);
    }
}

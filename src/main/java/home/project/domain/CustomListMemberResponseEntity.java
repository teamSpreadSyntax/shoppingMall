package home.project.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.http.HttpHeaders;
import java.util.List;

@Getter
@Setter
public class CustomListMemberResponseEntity<T> extends ResponseEntity<CustomListMemberResponseBody<T>>{
    public CustomListMemberResponseEntity(CustomListMemberResponseBody<T> body, HttpStatus status) {
        super(body, new HttpHeaders(), status);
    }

    public CustomListMemberResponseEntity(List<T> listData, String message, HttpStatus status) {
        super(new CustomListMemberResponseBody<>(listData, message), new HttpHeaders(), status);
    }
}

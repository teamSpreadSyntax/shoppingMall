package home.project.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import home.project.util.CustomOptionalSerializer;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Optional;
@Getter
@Setter
public class CustomResponseBody<T> {


    private Object result;
    private String responseMessage;
    private int status;

    public CustomResponseBody(Object result, String responseMessage, int status) {
        this.result = result;
        this.responseMessage = responseMessage;
        this.status = status;
    }


    public static class ListResult<T> {
        public long totalCount;
        public int page;
        public List<T> content;

        public ListResult(long totalCount, int page, List<T> content) {
            this.totalCount = totalCount;
            this.page = page;
            this.content = content;
        }
    }
}
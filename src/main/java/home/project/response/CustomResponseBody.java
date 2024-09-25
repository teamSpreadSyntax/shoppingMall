package home.project.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import home.project.util.CustomOptionalSerializer;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Optional;
@Getter
@Setter
public class CustomResponseBody {

    public static class ResponseBody<T> {
        @JsonSerialize(using = CustomOptionalSerializer.class)
        public Optional<T> optionalResult;
        public ListResult<T> listResult;
        public String responseMessage;
        public int status;

        public ResponseBody(ListResult<T> listResult, String responseMessage, int status) {
            this.listResult = listResult;
            this.responseMessage = responseMessage;
            this.status = status;
        }

        public ResponseBody(Optional<T> optionalResult, String responseMessage, int status) {
            this.optionalResult = optionalResult;
            this.responseMessage = responseMessage;
            this.status = status;
        }
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
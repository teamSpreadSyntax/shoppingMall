package home.project.response;

import java.util.List;

public class CustomListResponseBody<T> {
    public Result<T> result;
    public String responseMessage;
    public int status;

    public CustomListResponseBody(Result<T> result, String responseMessage, int status) {
        this.result = result;
        this.responseMessage = responseMessage;
        this.status = status;
    }

    public static class Result<T> {
        public long totalCount;
        public int page;
        public List<T> content;

        public Result(long totalCount, int page, List<T> content) {
            this.totalCount = totalCount;
            this.page = page;
            this.content = content;
        }
    }
}

package home.project.domain;

import org.springframework.data.domain.Page;

import java.util.List;

public class CustomPageResponseBody<T> {
    public Result<T> result;
    public String message;
    public int status;

    public CustomPageResponseBody(Result<T> result, String message, int status) {
        this.result = result;
        this.message = message;
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

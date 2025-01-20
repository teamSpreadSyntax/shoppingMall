package home.project.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CustomResponseBody<T> {
    @Schema(description = "응답 결과 데이터")
    private Object result;

    @Schema(description = "응답 메시지")
    private String responseMessage;

    @Schema(description = "HTTP 상태 코드", example = "200")
    private int status;

    public CustomResponseBody() {}  // <- 추가

    public CustomResponseBody(Object result, String responseMessage, int status) {
        this.result = result;
        this.responseMessage = responseMessage;
        this.status = status;
    }


    public static class ListResult<T> {
        @Schema(description = "전체 데이터 수", example = "100")
        public long totalCount;

        @Schema(description = "현재 페이지 번호", example = "0")
        public int page;

        @Schema(description = "데이터 목록")
        public List<T> content;

        public ListResult() {}  // <- 추가

        public ListResult(long totalCount, int page, List<T> content) {
            this.totalCount = totalCount;
            this.page = page;
            this.content = content;
        }
    }
}
package home.project.dto.responseDTO;

import home.project.domain.common.AnswerStatus;
import home.project.domain.common.QnAType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "QnA 응답")
public class QnAResponse {

    @Schema(description = "QnA ID", example = "1")
    private Long qnAId;

    @Schema(description = "QnA 유형", example = "PRODUCT")
    private QnAType qnAType;

    @Schema(description = "제목", example = "상품 배송 문의")
    private String subject;

    @Schema(description = "회원 이메일", example = "user@example.com")
    private String memberEmail;

    @Schema(description = "생성일")
    private LocalDateTime createAt;

    @Schema(description = "답변 상태", example = "ANSWERED")
    private AnswerStatus answerStatus;

    public QnAResponse(Long qnAId, QnAType qnAType, String subject, String memberEmail, LocalDateTime createAt, AnswerStatus answerStatus) {
        this.qnAId = qnAId;
        this.qnAType = qnAType;
        this.subject = subject;
        this.memberEmail = memberEmail;
        this.createAt = createAt;
        this.answerStatus = answerStatus;
    }
}

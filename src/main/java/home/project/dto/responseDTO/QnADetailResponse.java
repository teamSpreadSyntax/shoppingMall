package home.project.dto.responseDTO;

import home.project.domain.common.AnswerStatus;
import home.project.domain.common.QnAType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "QnA 상세 응답")
public class QnADetailResponse {

    @Schema(description = "QnA ID", example = "1")
    private Long id;

    @Schema(description = "QnA 유형", example = "PRODUCT")
    private QnAType qnAType;

    @Schema(description = "제목", example = "상품 배송 문의")
    private String subject;

    @Schema(description = "상품 번호", example = "PROD-2024-001")
    private String productNum;

    @Schema(description = "주문 번호", example = "ORD-2024-0001")
    private String orderNum;

    @Schema(description = "내용")
    private String description;

    @Schema(description = "회원 이메일", example = "user@example.com")
    private String memberEmail;

    @Schema(description = "생성일")
    private LocalDateTime createAt;

    @Schema(description = "답변")
    private String answer;

    @Schema(description = "답변 날짜")
    private LocalDateTime answerDate;

    @Schema(description = "답변자 이메일", example = "admin@example.com")
    private String answererEmail;

    @Schema(description = "답변 상태", example = "ANSWERED")
    private AnswerStatus answerStatus;

    public QnADetailResponse(Long id, QnAType qnAType, String subject, String productNum, String orderNum, String description, String memberEmail, LocalDateTime createAt, String answer, LocalDateTime answerDate, String answererEmail, AnswerStatus answerStatus) {
        this.id = id;
        this.qnAType = qnAType;
        this.subject = subject;
        this.productNum = productNum;
        this.orderNum = orderNum;
        this.description = description;
        this.memberEmail = memberEmail;
        this.createAt = createAt;
        this.answer = answer;
        this.answerDate = answerDate;
        this.answererEmail = answererEmail;
        this.answerStatus = answerStatus;
    }
}

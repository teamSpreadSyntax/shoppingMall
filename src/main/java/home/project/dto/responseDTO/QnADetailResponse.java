package home.project.dto.responseDTO;

import home.project.domain.AnswerStatus;
import home.project.domain.QnAType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class QnADetailResponse {
    private Long id;
    private QnAType qnAType;
    private String subject;
    private String productNum;
    private String orderNum;
    private String description;
    private String memberEmail;
    private LocalDateTime createAt;
    private String answer;
    private LocalDateTime answerDate;
    private String answererEmail;
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

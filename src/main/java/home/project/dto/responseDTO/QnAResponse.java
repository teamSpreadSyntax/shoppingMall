package home.project.dto.responseDTO;

import home.project.domain.common.AnswerStatus;
import home.project.domain.common.QnAType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class QnAResponse {

    private Long qnAId;

    private QnAType qnAType;

    private String subject;

    private String memberEmail;

    private LocalDateTime createAt;

    // 답변 상태 추가
    private AnswerStatus answerStatus;

    public QnAResponse(Long qnAId, QnAType qnAType, String subject,
                       String memberEmail, LocalDateTime createAt,
                       AnswerStatus answerStatus) {
        this.qnAId = qnAId;
        this.qnAType = qnAType;
        this.subject = subject;
        this.memberEmail = memberEmail;
        this.createAt = createAt;
        this.answerStatus = answerStatus;
    }
}
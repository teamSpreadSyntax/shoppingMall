package home.project.dto.responseDTO;

import home.project.domain.QnAType;
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


    public QnAResponse(Long qnAId, QnAType qnAType, String subject, String memberEmail, LocalDateTime createAt) {
        this.qnAId = qnAId;
        this.qnAType = qnAType;
        this.subject = subject;
        this.memberEmail = memberEmail;
        this.createAt = createAt;
    }
}

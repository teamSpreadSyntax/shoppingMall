package home.project.dto.responseDTO;

import home.project.domain.QnAType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class QnADetailResponse {

    private Long qnAId;

    private QnAType qnAType;

    private String subject;

    private String productNum;

    private String orderNum;

    private String description;

    private String memberEmail;

    private LocalDateTime createAt;


    public QnADetailResponse(Long qnAId, QnAType qnAType, String subject, String productNum, String orderNum, String description, String memberEmail, LocalDateTime createAt) {
        this.qnAId = qnAId;
        this.qnAType = qnAType;
        this.subject = subject;
        this.productNum = productNum;
        this.orderNum = orderNum;
        this.description = description;
        this.memberEmail = memberEmail;
        this.createAt = createAt;
    }
}

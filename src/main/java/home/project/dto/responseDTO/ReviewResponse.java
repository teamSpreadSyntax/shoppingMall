package home.project.dto.responseDTO;

import home.project.domain.Product;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewResponse {

    private Long reviewId;

    private String productName;

    private String memberEmail;

    private LocalDateTime createAt;

    public ReviewResponse(Long reviewId, String productName, String memberEmail, LocalDateTime createAt) {
        this.reviewId = reviewId;
        this.productName = productName;
        this.memberEmail = memberEmail;
        this.createAt = createAt;
    }
}

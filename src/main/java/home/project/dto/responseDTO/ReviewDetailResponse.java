package home.project.dto.responseDTO;

import home.project.domain.Rating;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewDetailResponse {
    private Long reviewId;

    private String memberEmail;

    private String productName;

    private LocalDateTime createAt;

    private Rating rating;

    private String description;

    private String imageUrl1;

    private String imageUrl2;

    private String imageUrl3;

    private Long helpful;

    public ReviewDetailResponse(Long reviewId, String memberEmail, String productName, LocalDateTime createAt, Rating rating, String description, String imageUrl1, String imageUrl2, String imageUrl3, Long helpful) {
        this.reviewId = reviewId;
        this.memberEmail = memberEmail;
        this.productName = productName;
        this.createAt = createAt;
        this.rating = rating;
        this.description = description;
        this.imageUrl1 = imageUrl1;
        this.imageUrl2 = imageUrl2;
        this.imageUrl3 = imageUrl3;
        this.helpful = helpful;
    }
}

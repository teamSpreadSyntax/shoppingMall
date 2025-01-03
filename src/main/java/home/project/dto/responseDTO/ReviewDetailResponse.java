package home.project.dto.responseDTO;

import home.project.domain.common.RatingType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Schema(description = "리뷰 상세 응답")
public class ReviewDetailResponse {

    @Schema(description = "리뷰 ID", example = "1")
    private Long reviewId;

    @Schema(description = "회원 이메일", example = "user@example.com")
    private String memberEmail;

    @Schema(description = "상품 이름", example = "블루 데님 자켓")
    private String productName;

    @Schema(description = "생성일")
    private LocalDateTime createAt;

    @Schema(description = "평점", example = "FIVE")
    private RatingType ratingType;

    @Schema(description = "리뷰 내용")
    private String description;

    @Schema(description = "이미지 URL", example = "https://example.com/image1.jpg")
    private List<String> imageUrls;

    @Schema(description = "도움이 됨 수", example = "10")
    private Long helpful;

    public ReviewDetailResponse(Long reviewId, String memberEmail, String productName, LocalDateTime createAt, RatingType ratingType, String description, List<String> imageUrls, Long helpful) {
        this.reviewId = reviewId;
        this.memberEmail = memberEmail;
        this.productName = productName;
        this.createAt = createAt;
        this.ratingType = ratingType;
        this.description = description;
        this.imageUrls = imageUrls;
        this.helpful = helpful;
    }
}

package home.project.dto.responseDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "리뷰 응답")
public class ReviewResponse {

    @Schema(description = "리뷰 ID", example = "1")
    private Long reviewId;

    @Schema(description = "상품 이름", example = "블루 데님 자켓")
    private String productName;

    @Schema(description = "회원 이메일", example = "user@example.com")
    private String memberEmail;

    @Schema(description = "생성일")
    private LocalDateTime createAt;

    public ReviewResponse(Long reviewId, String productName, String memberEmail, LocalDateTime createAt) {
        this.reviewId = reviewId;
        this.productName = productName;
        this.memberEmail = memberEmail;
        this.createAt = createAt;
    }
}

package home.project.dto.requestDTO;

import home.project.domain.common.RatingType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateReviewRequestDTO {

    @NotBlank(message = "내용을 입력해주세요.")
    @Schema(description = "내용")
    private String description;

    @NotBlank(message = "별점을 선택해주세요.")
    @Schema(description = "별점")
    private RatingType ratingType;
}

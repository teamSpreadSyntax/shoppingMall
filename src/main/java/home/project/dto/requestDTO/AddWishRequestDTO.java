package home.project.dto.requestDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddWishRequestDTO {

    @NotNull(message = "제품 ID는 필수입니다")
    @Schema(description = "제품 id", required = true)
    private Long productId;

}

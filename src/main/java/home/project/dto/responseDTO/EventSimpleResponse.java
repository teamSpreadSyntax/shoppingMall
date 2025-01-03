package home.project.dto.responseDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "이벤트 대표 이미지")
public class EventSimpleResponse {

    @Schema(description = "상품 ID", example = "1")
    private Long id;

    @Schema(description = "이미지 URL", example = "https://example.com/image.jpg")
    private String mainImageFile;

    public EventSimpleResponse(Long id, String mainImageFile) {
        this.id = id;
        this.mainImageFile = mainImageFile;
    }
}

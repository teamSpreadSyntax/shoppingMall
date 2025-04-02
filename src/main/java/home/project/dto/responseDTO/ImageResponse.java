package home.project.dto.responseDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Schema(description = "이미지 등록 응답")
public class ImageResponse {

    @Schema(description = "메인이미지")
    private String mainImageUrl;

    @Schema(description = "상세이미지")
    private List<String> descriptionImageUrls;

    public ImageResponse(String mainImageUrl, List<String> descriptionImageUrls) {
        this.mainImageUrl = mainImageUrl;
        this.descriptionImageUrls = descriptionImageUrls;
    }
}

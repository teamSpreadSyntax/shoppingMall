package home.project.dto.responseDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "카테고리 응답")
public class CategoryResponse {

    @Schema(description = "카테고리 ID", example = "1")
    private Long id;

    @Schema(description = "카테고리 코드", example = "0001")
    private String code;

    @Schema(description = "카테고리 이름", example = "패션")
    private String name;

    @Schema(description = "카테고리 레벨", example = "1")
    private Integer level;

    @Schema(description = "부모 카테고리 ID", example = "0")
    private Long parent;

    @Schema(description = "하위 카테고리 목록")
    private List<CategoryResponse> children;

    public CategoryResponse(Long id, String code, String name, Integer level, Long parent, List<CategoryResponse> children) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.level = level;
        this.parent = parent;
        this.children = children;
    }
}

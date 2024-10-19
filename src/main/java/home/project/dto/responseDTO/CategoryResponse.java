package home.project.dto.responseDTO;

import home.project.domain.Category;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CategoryResponse {

    private Long id;

    private String code;

    private String name;

    private Integer level;

    private Long parent;

    private List<CategoryResponse> children;

    public CategoryResponse(Long id, String code, String name, Integer level, Long parent, List<CategoryResponse> children){
        this.id = id;
        this.code = code;
        this.name = name;
        this.level = level;
        this.parent = parent;
        this.children = children;
    }

}

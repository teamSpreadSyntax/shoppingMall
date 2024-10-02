package home.project.dto.responseDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryResponse {

    private Long id;

    private String code;

    private String name;

    private Integer level;

    private Long parent;

    public CategoryResponse(Long id, String code, String name, Integer level, Long parent){
        this.id = id;
        this.code = code;
        this.name = name;
        this.level = level;
        this.parent = parent;
    }

}

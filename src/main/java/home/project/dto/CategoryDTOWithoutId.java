package home.project.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDTOWithoutId {

    @NotEmpty(message = "코드를 입력해주세요.")
    @Column(name = "category_code")
    private String code;

    @NotEmpty(message = "이름을 입력해주세요.")
    @Column(name = "category_name")
    private String name;

    @Column(name = "parent_code")
    private String parentCode;

    @NotNull(message = "레벨을 입력해주세요.")
    @Column(name = "level")
    private Integer level;
}

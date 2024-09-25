package home.project.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDTOWithoutId {

    @NotNull(message = "코드를 입력해주세요.")
    private String code;

    @NotEmpty(message = "이름을 입력해주세요.")
    private String name;

    @NotNull(message = "레벨을 입력해주세요.")
    private Integer level;

}

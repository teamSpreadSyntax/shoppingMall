package home.project.dto.requestDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindEmailRequestDTO {

    @NotEmpty(message = "이름을 입력해주세요.")
    @Schema(description = "사용자 이름", required = true)
    private String name;

    @NotEmpty(message = "전화번호를 입력해주세요.")
    @Schema(description = "사용자 전화번호", required = true)
    private String phone;
}
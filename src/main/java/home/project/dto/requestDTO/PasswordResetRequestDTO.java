package home.project.dto.requestDTO;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetRequestDTO {

    @NotEmpty(message = "이메일을 입력해주세요.")
    private String email;
}
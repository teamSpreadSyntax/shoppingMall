package home.project.dto.requestDTO;

import home.project.service.util.RegexPatterns;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetDTO {

    @NotEmpty(message = "비밀번호를 입력해주세요.")
    @Pattern(regexp = RegexPatterns.PASSWORD_PATTERN, message = RegexPatterns.PASSWORD_MESSAGE)
    @Schema(description = "새로 설정할 비밀번호", example = "Newpassword123", required = true)
    private String newPassword;

    @NotEmpty(message = "비밀번호를 다시 한번 입력해주세요.")
    @Pattern(regexp = RegexPatterns.PASSWORD_PATTERN, message = RegexPatterns.PASSWORD_MESSAGE)
    @Schema(description = "비밀번호 확인", example = "Newpassword123", required = true)
    private String passwordConfirm;
}
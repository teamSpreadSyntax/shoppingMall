package home.project.dto.requestDTO;

import home.project.service.util.RegexPatterns;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * 사용자 인증을 위한 요청 데이터 전송 객체(DTO)입니다.
 * 이 클래스는 클라이언트로부터 사용자 확인에 필요한 비밀번호를 받아 서버로 전달합니다.
 */
@Getter
@Setter
public class VerifyUserRequestDTO {

    /**
     * 사용자 확인을 위한 비밀번호입니다.
     * 이 필드는 비어있을 수 없으며, 지정된 패턴을 따라야 합니다.
     */
    @NotEmpty(message = "비밀번호를 입력해주세요.")
    @Pattern(regexp = RegexPatterns.PASSWORD_PATTERN, message = RegexPatterns.PASSWORD_MESSAGE)
    @Schema(description = "사용자 비밀번호", required = true)
    private String password;

}
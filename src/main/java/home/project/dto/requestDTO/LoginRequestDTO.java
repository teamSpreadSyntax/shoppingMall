package home.project.dto.requestDTO;

import home.project.service.util.RegexPatterns;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사용자 로그인을 위한 요청 데이터 전송 객체(DTO)입니다.
 * 이 클래스는 클라이언트로부터 로그인에 필요한 정보를 받아 서버로 전달합니다.
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDTO {

    /**
     * 사용자의 이메일 주소입니다.
     * 이 필드는 비어있을 수 없으며, 유효한 이메일 형식이어야 합니다.
     */
    @NotEmpty(message = "이메일을 입력해주세요.")
    @Pattern(regexp = RegexPatterns.EMAIL_PATTERN, message = RegexPatterns.EMAIL_MESSAGE)
    private String email;

    /**
     * 사용자의 비밀번호입니다.
     * 이 필드는 비어있을 수 없으며, 지정된 패턴을 따라야 합니다.
     */
    @NotEmpty(message = "비밀번호를 입력해주세요.")
    @Pattern(regexp = RegexPatterns.PASSWORD_PATTERN, message = RegexPatterns.PASSWORD_MESSAGE)
    private String password;

}
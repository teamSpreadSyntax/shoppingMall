package home.project.dto.requestDTO;

import home.project.service.util.RegexPatterns;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * 회원 정보 업데이트를 위한 요청 데이터 전송 객체(DTO)입니다.
 * 이 클래스는 클라이언트로부터 회원 정보 수정에 필요한 데이터를 받아 서버로 전달합니다.
 */
@Getter
@Setter
public class UpdateMemberRequestDTO {

    /**
     * 수정할 회원의 이메일 주소입니다.
     * 이 필드는 비어있을 수 없으며, 유효한 이메일 형식이어야 합니다.
     */
    @NotEmpty(message = "이메일을 입력해주세요.")
    @Pattern(regexp = RegexPatterns.EMAIL_PATTERN, message = RegexPatterns.EMAIL_MESSAGE)
    @Schema(description = "사용자 이메일", required = true)
    private String email;

    /**
     * 수정할 회원의 새 비밀번호입니다.
     * 이 필드는 비어있을 수 없으며, 지정된 패턴을 따라야 합니다.
     */
    @NotEmpty(message = "비밀번호를 입력해주세요.")
    @Pattern(regexp = RegexPatterns.PASSWORD_PATTERN, message = RegexPatterns.PASSWORD_MESSAGE)
    @Schema(description = "사용자 비밀번호", required = true)
    private String password;

    /**
     * 수정할 회원의 새 비밀번호 확인입니다.
     * 이 필드는 비어있을 수 없으며, 'password' 필드와 일치해야 합니다.
     */
    @NotEmpty(message = "비밀번호를 다시 한번 입력해주세요.")
    @Pattern(regexp = RegexPatterns.PASSWORD_PATTERN, message = RegexPatterns.PASSWORD_MESSAGE)
    @Schema(description = "사용자 비밀번호 확인", required = true)
    private String passwordConfirm;

    /**
     * 수정할 회원의 이름입니다.
     * 이 필드는 비어있을 수 없습니다.
     */
    @NotEmpty(message = "이름을 입력해주세요.")
    @Schema(description = "사용자 이름", required = true)
    private String name;

    /**
     * 수정할 회원의 전화번호입니다.
     * 이 필드는 비어있을 수 없으며, 유효한 전화번호 형식이어야 합니다.
     */
    @NotEmpty(message = "전화번호를 입력해주세요.")
    @Pattern(regexp = RegexPatterns.PHONE_PATTERN, message = RegexPatterns.PHONE_MESSAGE)
    @Schema(description = "사용자 전화번호", required = true)
    private String phone;

}
package home.project.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import home.project.repository.MemberRepository;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.models.annotations.OpenAPI30;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberDTOWithoutId {

    @NotEmpty(message = "이메일을 입력해주세요")
    @Pattern(regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])+\\.[a-zA-Z]{2,3}$", message = "이메일 형식이 올바르지 않습니다")
    @Schema(description = "사용자 이메일", required = true)
    private String email;

    @NotEmpty(message = "비밀번호를 입력해주세요")
    @Schema(description = "사용자 비밀번호", required = true)
    private String password;

    @NotEmpty(message = "회원 이름을 입력해주세요")
    @Schema(description = "사용자 이름", required = true)
    private String name;

    @NotEmpty(message = "전화번호를 입력해주세요")
    @Pattern(regexp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다")
    @Schema(description = "사용자 전화번호", required = true)
    private String phone;
}

package home.project.dto.responseDTO;

import home.project.domain.member.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "역할 응답")
public class RoleResponse {

    @Schema(description = "역할 ID", example = "1")
    private Long id;

    @Schema(description = "사용자의 권한 유형", example = "ADMIN")
    private RoleType role;

    @Schema(description = "역할과 연관된 사용자 이름", example = "홍길동")
    private String name;

    public RoleResponse(Long id, RoleType role, String name) {
        this.id = id;
        this.name = name;
        this.role = role;
    }
}

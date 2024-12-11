package home.project.dto.responseDTO;

import home.project.domain.member.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "토큰 응답")
public class TokenResponse {

    @Schema(description = "인증 부여 유형", example = "Bearer")
    private String grantType;

    @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;

    @Schema(description = "사용자의 권한 유형", example = "USER")
    private RoleType role;
}

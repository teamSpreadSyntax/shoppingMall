package home.project.dto.responseDTO;

import home.project.domain.RoleType;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponseDTO {
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private RoleType role;
}


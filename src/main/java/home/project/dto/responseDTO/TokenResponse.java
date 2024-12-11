package home.project.dto.responseDTO;

import home.project.domain.member.RoleType;
import lombok.*;

/**
 * 인증 토큰 정보를 클라이언트에게 반환하기 위한 응답 데이터 전송 객체(DTO)입니다.
 * 이 클래스는 서버에서 클라이언트로 인증 토큰과 관련된 정보를 전달하는 데 사용됩니다.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponse {

    /**
     * 인증 부여 유형입니다. 일반적으로 "Bearer"가 사용됩니다.
     */
    private String grantType;

    /**
     * 액세스 토큰입니다. 클라이언트가 보호된 리소스에 접근할 때 사용합니다.
     */
    private String accessToken;

    /**
     * 리프레시 토큰입니다. 액세스 토큰이 만료되었을 때 새로운 액세스 토큰을 얻는 데 사용됩니다.
     */
    private String refreshToken;

    /**
     * 토큰과 연관된 사용자의 권한 유형입니다.
     */
    private RoleType role;

}
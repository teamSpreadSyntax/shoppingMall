package home.project.domain;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenDto {
    private String grantType;//WT에 대한 인증 타입
    private String accessToken;//인증된 유저인지 서버에서 검증하는 토큰, client쪽에서 요청 헤더에 담아보내는 토큰
    private String refreshToken;// access token이 만료되거나 잘못된 토큰일 경우 refresh token을 사용해 유저를 검증합니다. 만약 refresh token을 검증하여 인증된 유저일 경우 access token을 재발행 해줍니다.
//    private Long accessTokenExpireln;
//    private Long refreshTokenExpiresin;
//    private String authority;
//    private String info;
}


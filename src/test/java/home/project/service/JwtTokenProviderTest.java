//package home.project.service;
//
//import home.project.domain.TokenDto;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//
//import javax.crypto.spec.SecretKeySpec;
//import java.security.Key;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.Date;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class JwtTokenProviderTest {
//    private static final String SECRET_KEY = "0k7kzHlg8LLJYy4hGibQKsUc0qWpVG3cJvYpgqC44DM8r3qg4jaDQxlOs/dYEIUvdLULZ+aAEWghfrm4ZUityg==";
//    private static final String USERNAME = "testuser";
//    private static final String ROLE = "ROLE_USER";
//
//    private JwtTokenProvider tokenProvider;
//    private Key key;
//
//    @BeforeEach
//    void setUp(){
//        key = new SecretKeySpec(SECRET_KEY.getBytes(), SignatureAlgorithm.HS256.getJcaName());
//        //JCA에서 제공하는 클래스로 비밀키를 나타냄. 문자열 형태의 비밀키를 바이트 배열로 변환
//        //JWT토큰 서명에 사용되는 알고리즘을 나타내며 HS256알고리즘을 사용.
//        //key는 HS256알고리즘을 사용하는 비밀키 객체가 된다.
//        tokenProvider = new JwtTokenProvider(SECRET_KEY);
//        //클래스 생성자에 비밀키 문자열을 전달하여 인스턴스 생성
//        //생성된 비밀키와 인스턴스는 JWT 토큰의 생성 검증 및 복호화 과정에서 사용
//        //비밀키는 토큰의 서명과 검증에 필수적!
//    }
//    @DisplayName("testGenerateToken 메서드는 generateToken 메서드를 호출하여 반환된 TokenDto 객체를 검증")
//    @Test
//    void generateToken() {
//        //given
//        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                USERNAME,
//                null,
//                Collections.singletonList(new SimpleGrantedAuthority(ROLE)));
//        //when
//        TokenDto tokenDto = tokenProvider.generateToken(authentication);
//        //then
//        assertNotNull(tokenDto.getAccessToken()); // 엑세스토큰값이 널값이 아닌지
//        assertNotNull(tokenDto.getRefreshToken()); // 리프레시토큰값이 널값이 아닌지
//        assertNotNull(tokenDto.getGrantType()); //권한타입이 널값이 아닌지
//        assertEquals("Bearer", tokenDto.getGrantType()); //권한타입이 Bearer과 같은지. Bearer 인증 스킴은 토큰 기반 인증에 사용
//    }
//
//    @DisplayName("엑세스 토큰을 기반으로 Authentication을 반환하는지 테스트")
//    @Test
//    void getAuthentication() {
//        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                USERNAME,
//                null,
//                Collections.singletonList(new SimpleGrantedAuthority(ROLE))
//        );
//
//        TokenDto tokenDto = tokenProvider.generateToken(authentication);
//        String accessToken = tokenDto.getAccessToken();
//
//        Authentication result = tokenProvider.getAuthentication(accessToken);
//
//        assertNotNull(result); //Authentication가 널이 아닌지 확인
//        assertEquals(USERNAME, result.getName()); //result의 사용자 이름과 기댓값 username을 비교
//        assertTrue(result.getAuthorities().contains(new SimpleGrantedAuthority(ROLE)));
//        //getAuthorities 사용자의 권한정보를 가져와서 기댓값 권환이 포함되어있는지 확인
//    }
//
//    @DisplayName("주어진 JWT 토큰의 유효성을 테스트")
//    @Test
//    void validateToken() {
//        Authentication authentication = new UsernamePasswordAuthenticationToken(
//                USERNAME,
//                null,
//                Collections.singletonList(new SimpleGrantedAuthority(ROLE))
//        );
//
//        TokenDto tokenDto = tokenProvider.generateToken(authentication);
//        String accessToken = tokenDto.getAccessToken();
//
//        assertTrue(tokenProvider.validateToken(accessToken));
//
//        // 만료된 토큰
//        Claims claims = Jwts.claims().setSubject(USERNAME);
//        Date now = new Date();
//        claims.setExpiration(new Date(now.getTime() - 60000)); // 1분 전에 만료
//        String expiredToken = Jwts.builder()//jwt토큰 생성
//                .setClaims(claims)
//                .signWith(key, SignatureAlgorithm.HS256)//서명 알고리즘을 사용하여 토큰을 구성하고
//                .compact();//compact 메서드를 호출하여 문자열로 변환
//
//        assertFalse(tokenProvider.validateToken(expiredToken));//만료된 토큰 전달후 반환값이 false인지 확인
//    }
//}
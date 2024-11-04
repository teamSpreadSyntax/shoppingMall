package home.project.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import java.io.IOException;

public class FirebaseAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public FirebaseAuthenticationFilter(AuthenticationManager authenticationManager) {
        super("/api/**"); // 특정 경로에만 Firebase 인증 적용
        setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("JWT Token is missing");
        }
        String idToken = token.substring(7); // "Bearer " 제거

        // 여기서 uid를 null로 설정하고, 두 번째 인수로 전달
        return getAuthenticationManager().authenticate(new FirebaseAuthenticationToken(null, idToken));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        // 인증 성공 시 다음 필터로 요청 전달
        chain.doFilter(request, response);
    }
}

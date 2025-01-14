package home.project.service.integration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.List;

public class FirebaseAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private List<String> permitAllPaths;

    public FirebaseAuthenticationFilter(AuthenticationManager authenticationManager) {
        // 소셜 로그인 관련 URL 패턴만 지정
        super(new AntPathRequestMatcher("/api/auth/social/**")); // 소셜 로그인 URL만 처리
        setAuthenticationManager(authenticationManager);
    }

    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        // 소셜 로그인 요청인 경우에만 인증 필요
        String requestURI = request.getRequestURI();
        if (!requestURI.startsWith("/api/auth/social/")) {
            return false;
        }

        return permitAllPaths == null || permitAllPaths.stream()
                .noneMatch(pattern -> new AntPathRequestMatcher(pattern).matches(request));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("JWT Token is missing");
        }

        String idToken = token.substring(7);
        return getAuthenticationManager().authenticate(new FirebaseAuthenticationToken(null, idToken));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
        chain.doFilter(request, response);
    }

    public void setPermitAllPaths(List<String> permitAllPaths) {
        this.permitAllPaths = permitAllPaths;
    }
}
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
import java.util.List;

// FirebaseAuthenticationFilter.java

public class FirebaseAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final List<String> permitAllUrls;

    public FirebaseAuthenticationFilter(AuthenticationManager authenticationManager, List<String> permitAllUrls) {
        super("/api/**");
        setAuthenticationManager(authenticationManager);
        this.permitAllUrls = permitAllUrls;
    }

    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        String requestURI = request.getRequestURI();
        return permitAllUrls.stream().noneMatch(requestURI::startsWith);
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
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        chain.doFilter(request, response);
    }
}

package home.project.service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private FilterChain filterChain;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenProvider);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("shouldNotFilter가 true일 때 필터를 건너뛴다")
    void shouldNotFilterSkipsFilter() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/auth/login");

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtTokenProvider);
    }

    @Test
    @DisplayName("유효한 JWT 토큰으로 SecurityContext에 인증 정보를 설정한다")
    void validTokenSetsAuthentication() throws Exception {
        String token = "validToken";

        when(request.getRequestURI()).thenReturn("/api/secure");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenProvider.validateToken(token)).thenReturn(true);
        when(jwtTokenProvider.getAuthentication(token)).thenReturn(authentication);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isEqualTo(authentication);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("유효하지 않은 JWT 토큰으로 인증 정보를 설정하지 않는다")
    void invalidTokenDoesNotSetAuthentication() throws Exception {
        String token = "invalidToken";

        when(request.getRequestURI()).thenReturn("/api/secure");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenProvider.validateToken(token)).thenReturn(false);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Authorization 헤더가 없을 때 인증 정보를 설정하지 않는다")
    void noAuthorizationHeaderDoesNotSetAuthentication() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/secure");
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("shouldNotFilter가 false일 때 필터를 수행한다")
    void shouldNotFilterPerformsFiltering() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/secure");

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }
}

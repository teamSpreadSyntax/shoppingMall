package home.project.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import home.project.config.JwtAuthenticationEntryPoint;
import home.project.exceptions.CustomAccessDeniedHandler;
import home.project.exceptions.CustomLogoutSuccessHandler;
import home.project.service.util.*;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final SecurityPermissionsConfig securityPermissions;
    private final FirebaseAuthenticationProvider firebaseAuthenticationProvider;


    @Bean
    public AuthenticationManager firebaseAuthenticationManager() {
        return new ProviderManager(Arrays.asList(firebaseAuthenticationProvider));
    }

    @Bean
    public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "https://localhost:5173", "https://projectkkk.vercel.app", "https://localhost:63342", "http://localhost:63342", "https://www.projectkkk.com"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    @Primary
    public AuthenticationManager userDetailsAuthenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder, UserDetailsService userDetailsService) throws Exception {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);
        return new ProviderManager(provider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(new CustomOptionalSerializer());
        objectMapper.registerModule(module);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler(ObjectMapper objectMapper) {
        return new CustomAccessDeniedHandler(objectMapper);
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        FirebaseAuthenticationFilter firebaseFilter = new FirebaseAuthenticationFilter(firebaseAuthenticationManager());
        firebaseFilter.setPermitAllPaths(securityPermissions.getPermitAll());

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives(
                                        "default-src 'self'; " +
                                                "script-src 'self' 'sha256-P5polb1UreUSOe5V/Pv7tc+yeZuJXiOi/3fqhGsU7BE=' blob: https://projectkkk.com https://www.projectkkk.com; " +
                                                "style-src 'self' 'sha256-YOUR_STYLE_SHA_HASH' https://projectkkk.com https://www.projectkkk.com; " +
                                                "img-src 'self' data: blob: https://projectkkk.com https://www.projectkkk.com; " +
                                                "connect-src 'self' https: wss: blob:; " +
                                                "font-src 'self' data: blob:; " +
                                                "frame-src 'self' https://projectkkk.com https://www.projectkkk.com; " +
                                                "frame-ancestors 'self' https://projectkkk.com https://www.projectkkk.com; " +
                                                "worker-src 'self' blob:; " +
                                                "base-uri 'self'; " +
                                                "object-src 'none';"
                                )
                        )
                        .frameOptions(frame -> frame.sameOrigin())
                )
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(requests -> {
                    // 소셜 로그인 경로는 Firebase 인증 사용
                    requests.requestMatchers("/api/auth/social/**").permitAll();

                    securityPermissions.getPermitAll().forEach(pattern ->
                            requests.requestMatchers(pattern).permitAll()
                    );

                    securityPermissions.getHasAnyRole().forEach((roles, patterns) ->
                            patterns.forEach(pattern ->
                                    requests.requestMatchers(pattern).hasAnyRole(roles.split("_"))
                            )
                    );

                    securityPermissions.getHasRole().forEach((role, patterns) ->
                            patterns.forEach(pattern ->
                                    requests.requestMatchers(pattern).hasRole(role)
                            )
                    );
                    requests.requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll();
                    requests.requestMatchers("/ws/**").permitAll();
                    requests.anyRequest().permitAll();
                })
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .permitAll())
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                .authenticationEntryPoint(jwtAuthenticationEntryPoint())
                                .accessDeniedHandler(accessDeniedHandler(objectMapper()))
                )
                // JWT 필터를 먼저 적용하고, Firebase 필터는 소셜 로그인 경로에만 적용
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(firebaseFilter, JwtAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutUrl("/api/logout")
                        .logoutSuccessHandler(new CustomLogoutSuccessHandler())
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }
}
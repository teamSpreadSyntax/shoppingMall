package home.project.service.integration;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class FirebaseAuthenticationToken extends AbstractAuthenticationToken {
    private final String uid;  // Firebase 사용자 ID
    private final Object credentials;  // 토큰 정보

    // 인증되지 않은 토큰 생성을 위한 생성자
    public FirebaseAuthenticationToken(String uid, Object credentials) {
        super(null);
        this.uid = uid;
        this.credentials = credentials;
        setAuthenticated(false);
    }

    // 인증된 토큰 생성을 위한 생성자
    public FirebaseAuthenticationToken(String uid, Object credentials,
                                       Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.uid = uid;
        this.credentials = credentials;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return uid;
    }
}
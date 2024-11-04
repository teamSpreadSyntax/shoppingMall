package home.project.util;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;

public class FirebaseAuthenticationToken extends AbstractAuthenticationToken {
    private final String uid;  // Firebase 사용자 ID
    private final Object credentials;  // 토큰 정보

    public FirebaseAuthenticationToken(String uid, Object credentials) {
        super(null);
        this.uid = uid;
        this.credentials = credentials;
        setAuthenticated(true);  // 인증된 상태로 설정
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return uid;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return null;  // 권한 관리가 필요하면 설정
    }
}

package home.project.util;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

@Component
public class FirebaseAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String idToken = (String) authentication.getCredentials();
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String uid = decodedToken.getUid();
            // uid 기반 사용자 정보 반환
            return new FirebaseAuthenticationToken(uid, decodedToken);
        } catch (Exception e) {
            throw new RuntimeException("Firebase authentication failed", e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return FirebaseAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
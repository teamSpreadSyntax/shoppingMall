package home.project.service.integration;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class FirebaseAuthenticationProvider implements AuthenticationProvider {
    private static final Logger logger = LoggerFactory.getLogger(FirebaseAuthenticationProvider.class);

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            logger.debug("Starting Firebase authentication process");

            // 토큰 가져오기
            String idToken = (String) authentication.getCredentials();
            if (idToken == null || idToken.isEmpty()) {
                logger.error("ID token is null or empty");
                throw new IllegalArgumentException("FirebaseTokenBlank");
            }

            logger.debug("Verifying Firebase ID token");
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String uid = decodedToken.getUid();

            logger.info("Successfully authenticated Firebase user: {}", uid);

            // 인증 객체 생성 및 반환
            return new FirebaseAuthenticationToken(
                    uid,
                    decodedToken,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            );

        } catch (FirebaseAuthException e) {
            logger.error("Firebase authentication failed: {}", e.getMessage(), e);
            throw new RuntimeException("FirebaseAuthenticationFailed", e);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument during authentication: {}", e.getMessage(), e);
            throw new RuntimeException("InvalidAuthenticationArgument", e);
        } catch (Exception e) {
            logger.error("Unexpected error during Firebase authentication: {}", e.getMessage(), e);
            throw new RuntimeException("FirebaseAuthenticationError", e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return FirebaseAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
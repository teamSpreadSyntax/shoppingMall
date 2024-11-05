package home.project.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


public class FirebaseAuthController {

    @PostMapping("/firebase")
    public ResponseEntity<String> authenticateWithFirebase(@RequestBody String idToken) {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String uid = decodedToken.getUid();
            return ResponseEntity.ok("User authenticated with uid: " + uid);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid token");
        }
    }
}
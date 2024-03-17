//package home.project.service;
//
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Service;
//
//import java.util.Date;
//
//@Service
//public class JwtTokenProvider {
//
//    @Value("${jwt.secret}")
//    private String secretKey;
//
//    @Value("${jwt.expiration}")
//    private long validityInMilliseconds;
//
//    public String createToken(Authentication authentication) {
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//
//        Date now = new Date();
//        Date validity = new Date(now.getTime() + validityInMilliseconds);
//
//        return Jwts.builder()
//                .setSubject(userDetails.getUsername())
//                .setIssuedAt(now)
//                .setExpiration(validity)
//                .signWith(SignatureAlgorithm.HS256, secretKey)
//                .compact();
//    }
//
//    // 토큰에서 사용자 이름 추출
//    public String getUsernameFromToken(String token) {
//        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
//    }
//
//    // 토큰 유효성 검사
//    public boolean validateToken(String token) {
//        try {
//            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//}

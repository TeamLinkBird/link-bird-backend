package com.example.demo.common.utility;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.time.Duration;
import java.util.Date;

public class JwtUtility {
    public static String makeJwtToken() {
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // (1)
                .setIssuer("fresh") // (2)
                .setIssuedAt(now) // (3)
                .setExpiration(new Date(now.getTime() + Duration.ofMinutes(30).toMillis())) // 만료 시간은 30분
                .claim("id", "신동철") // 비공개 클레임
                //.claim("email", "ajufresh@gmail.com") // // 비공개 클레임
                .signWith(SignatureAlgorithm.HS256, "secret") // 서명부분  deprecated 되어 추후 변경 필요 있다.
                .compact();
    }

    public static Claims parseJwtToken(String authorizationHeader) {
        validationAuthorizationHeader(authorizationHeader); // (1)
        String token = extractToken(authorizationHeader); // (2)

        return Jwts.parser()
                .setSigningKey("secret") // (3)
                .parseClaimsJws(token) // (4)
                .getBody();
    }


    private static void validationAuthorizationHeader(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            throw new IllegalArgumentException();
        }
    }

    private static String extractToken(String authorizationHeader) {
        return authorizationHeader.substring("Bearer ".length());
    }
}

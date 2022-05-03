package com.example.demo.common.utility;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;

@Slf4j
public class JwtUtility {

    public static HashMap<String, String> getSocialToken(HttpServletRequest request, String secretKey) throws Exception {
        HashMap<String, String> dataMap;
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        dataMap = JwtUtility.getClaimData(authorizationHeader, secretKey, "access_Token", "refresh_Token", "id");
        return dataMap;
        //getClaimData 에서
        //parseToken 에서
        // IllegalArgumentException , IndexOutOfBoundsException ,  NullPointerException
        //JWT 예외 UnsupportedJwtException,MalformedJwtException,SignatureException,ExpiredJwtException,IllegalArgumentException,
        //getClaimData 자체에서
        //NullPointerException
    }

    //로컬 종합 토큰 생성후 반환
    //input : 로컬 access,refresh Token 유효시간, dataMap(id or id,소셜 access_Token,소셜 refresh_Token), jwt암호화 할 시크릿 키
    //output : tokenMap
    public static HashMap<String, String> makeToken(
            Long accessTokenTime, Long refreshTokenTime, HashMap<String, String> dataMap, String secretKey, String refreshTokensecretKey){
        HashMap<String, String> tokenMap = new HashMap<>();

        String access_Token = makeJwtToken(accessTokenTime, dataMap, secretKey);
        String refresh_Token = createRefreshToken(refreshTokenTime, access_Token, dataMap, refreshTokensecretKey);

        tokenMap.put("access_Token", access_Token);
        tokenMap.put("refresh_Token", refresh_Token);

        return tokenMap;
    }

    //로컬 access 토큰 생성후 반환
    // input :   dataMap에 id 만 들어오거나  소셜accessToken,소셜refreshToken 들어오거나 둘 중하나
    // output : 로컬 AccessToken
    public static String makeJwtToken(Long accessTokenTime, HashMap<String, String> dataMap, String secretKey) {

        boolean flag = false; // false: 비회원 로그인 인 경우 ( 고유 id만 담김 )
        if (dataMap.get("access_Token") != null)
            flag = true;

        Date expireTime = new Date(System.currentTimeMillis() + accessTokenTime);
        JwtBuilder jwtBuilder = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // (1)
                .setSubject("Valid")
                .setIssuer("MrShin")
                .setIssuedAt(new Date())
                .setExpiration(expireTime); // 만료 시간은 properties에 설정
        if (flag) { //소셜 토큰 정보가 있을 경우
            jwtBuilder
                    .claim("access_Token", dataMap.get("access_Token"))// 소셜 액세스 토큰
                    .claim("refresh_Token", dataMap.get("refresh_Token")); // 소셜 리프레시 토큰
        }
        else{   //비회원 로그인일 경우
            jwtBuilder
                    .claim("id", dataMap.get("id"));// 비공개 클레임
        }


        return jwtBuilder.signWith(SignatureAlgorithm.HS256, secretKey).compact(); // 서명부분  deprecated 되어 추후 변경 필요 있다.
    }

    //로컬 refresh 토큰 생성후 반환
    private static String createRefreshToken(Long refreshTokenTime, String server_access_Token, HashMap<String, String> social_Token, String refreshTokensecretKey) {

        Date expireTime = new Date(System.currentTimeMillis() + refreshTokenTime);

        JwtBuilder jwtBuilder = Jwts.builder()
                .setSubject("valid")
                .setIssuedAt(new Date())                     // 발행일자를 담아서 암호화
                .signWith(SignatureAlgorithm.HS256, server_access_Token+refreshTokensecretKey)
                .setExpiration(expireTime);

        if(social_Token!=null){
            String access_Token = social_Token.get("access_Token");
            String refresh_Token = social_Token.get("refresh_Token");
            jwtBuilder
                    .claim("access_Token", access_Token)// 소셜 액세스 토큰
                    .claim("refresh_Token", refresh_Token); // 소셜 리프레시 토큰
        }

        return jwtBuilder.compact();
    }




    //로컬 access 토큰 검증   true: 유효 , false : 만료
    public static Boolean isValidAccessToken(String jwtsecretKey, String access_Token) throws Exception{
        Date access_Token_Time = Jwts.parser()
                .setSigningKey(jwtsecretKey)
                .parseClaimsJws(access_Token)
                .getBody()
                .getExpiration();

        return true;
    }

    //로컬 refresh 토큰 DB 검증  , 유효하면 true , 무효하면 false
    public static boolean isValidRefreshToken(String client_refresh_Token, String db_refresh_Token) {
        if (client_refresh_Token == null || db_refresh_Token == null) {
            log.info("client or DB 의 refresh_Token이 존재하지 않습니다.");
            return false;
        }
        return db_refresh_Token.equals(client_refresh_Token);
    }

    //로컬 refresh 만료 검사  , 유효하면 false , 만료면 예외처리
    //input : tokenMap(서버 access_Token , 서버 refresh_Token)
    public static boolean isExpiredRefreshToken(HashMap<String, String> tokenMap) {
        String access_Token = tokenMap.get("access_Token");
        String refresh_Token = tokenMap.get("refresh_Token");
        try {
            Jwts.parser()
                    .setSigningKey(access_Token)
                    .parseClaimsJws(refresh_Token)
                    .getBody()
                    .getExpiration();
        }catch(ExpiredJwtException e){
            throw new ExpiredJwtException(null,null,"refresh_Token expired");
        }

        return false;
    }

    public static HashMap<String, Date> getTokenExpiration(HashMap<String, String> tokenMap, String jwtsecretKey) {
        String access_Token = tokenMap.get("access_Token");
        String refresh_Token = tokenMap.get("refresh_Token");

        HashMap<String, Date> dateMap = new HashMap<>();
        Date access_Token_Time = null;
        Date refresh_Token_Time = null;

        access_Token_Time = Jwts.parser()
                .setSigningKey(jwtsecretKey)
                .parseClaimsJws(access_Token)
                .getBody()
                .getExpiration();


        refresh_Token_Time = Jwts.parser()
                .setSigningKey(access_Token)
                .parseClaimsJws(refresh_Token)
                .getBody()
                .getExpiration();

        Long millis = access_Token_Time.getTime() - System.currentTimeMillis();
        int second = millis.intValue() / 60;
        int minute = second / 60;
        int hour = minute / 60;
        int day = hour / 24;

        log.info("엑세스 토큰 남은 시간 : {}, 일 : {} ", hour, day);

        millis = refresh_Token_Time.getTime() - System.currentTimeMillis();
        second = millis.intValue() / 60;
        minute = second / 60;
        hour = minute / 60;
        day = hour / 24;

        log.info("리프레시토큰 토큰 남은 시간 : {}, 일 : {} ", hour, day);

        dateMap.put("access_Token_Time", access_Token_Time);
        dateMap.put("refresh_Token_Time", refresh_Token_Time);

        return dateMap;
    }

    //input : server_access_Token , server_refresh_Token
    //output : HashMap(social_access_Token,social_refresh_Token)
    public static HashMap<String, String> getClaimDataFromRefreshToken(String server_access_Token, String server_refresh_Token, String refreshTokensecretKey) throws Exception{
        Claims claims = Jwts.parser()
                .setSigningKey(server_access_Token+refreshTokensecretKey)
                .parseClaimsJws(server_refresh_Token)  // UnsupportedJwtException,MalformedJwtException,SignatureException,ExpiredJwtException,IllegalArgumentException
                .getBody();
        String social_access_Token = (String) claims.get("access_Token");
        String social_refresh_Token = (String) claims.get("refresh_Token");

        HashMap<String, String> social_Token = new HashMap<>();
        social_Token.put("social_access_Token",social_access_Token);
        social_Token.put("social_refresh_Token",social_refresh_Token);
        return social_Token;
    }

    // authorizationHeader 로부터 HashMap 데이터 획득
    public static HashMap<String, String> getClaimData(String authorizationHeader, String secretKey, String... keys) throws Exception {

        HashMap<String, String> dataMap = new HashMap<>();
        Claims claims = parseToken(authorizationHeader, secretKey);

        for (int idx = 0; idx < keys.length; idx++)
            dataMap.put(keys[idx], (String) claims.get(keys[idx]));


        return dataMap;
        //getClaimData 에서
        //parseToken 에서
        // IllegalArgumentException , IndexOutOfBoundsException ,  NullPointerException
        //JWT 예외 UnsupportedJwtException,MalformedJwtException,SignatureException,ExpiredJwtException,IllegalArgumentException,
        //getClaimData 자체에서
        //NullPointerException

    }

    public static Claims parseToken(String authorizationHeader, String secretKey) throws Exception {
        validationAuthorizationHeader(authorizationHeader); // IllegalArgumentException
        String token = extractAccessToken(authorizationHeader); //  IndexOutOfBoundsException ,  NullPointerException

        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)  // UnsupportedJwtException,MalformedJwtException,SignatureException,ExpiredJwtException,IllegalArgumentException
                .getBody();

        // IllegalArgumentException , IndexOutOfBoundsException ,  NullPointerException
        //JWT 예외 UnsupportedJwtException,MalformedJwtException,SignatureException,ExpiredJwtException,IllegalArgumentException,
    }

    public static void validationAuthorizationHeader(String header) throws Exception {
        if (header == null || !header.startsWith("Bearer ")) {
            throw new IllegalArgumentException();
        }
        //예외 : IllegalArgumentException
    }

    public static String extractAccessToken(String authorizationHeader) throws Exception {
        return authorizationHeader.substring("Bearer ".length());
        //예외 : IndexOutOfBoundsException ,  NullPointerException
    }

}

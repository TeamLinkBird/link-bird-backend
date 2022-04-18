package com.example.demo.common.utility;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;

@Slf4j
public class JwtUtility {

    //로컬 토큰 생성후 반환
    //input : 로컬 access,refresh Token 유효시간, dataMap(id or id,소셜 access_Token,소셜 refresh_Token), jwt암호화 할 시크릿 키
    //output : tokenMap
    public static HashMap<String,String> makeToken(
            int accessTokenTime ,int refreshTokenTime ,HashMap<String,String> dataMap ,String secretKey ){
        HashMap<String,String> tokenMap = new HashMap<>();

        String access_Token = makeJwtToken(accessTokenTime,dataMap,secretKey);
        String refresh_Token = createRefreshToken(refreshTokenTime, access_Token);

        tokenMap.put("access_Token",access_Token);
        tokenMap.put("refresh_Token",refresh_Token);

        return tokenMap;
    }

    //로컬 access 토큰 생성후 반환
    // input :   dataMap에 id 만들어오거나  소셜accessToken,소셜refreshToken도 같이 들어오거나 둘 중하나
    // output : 로컬 AccessToken
    private static String makeJwtToken(int accessTokenTime,HashMap<String,String> dataMap,String secretKey) {

        boolean flag = false; // false: 비회원 로그인 인 경우 ( 고유 id만 담김 )
        if(dataMap.get("access_Token") != null )
            flag=true;

        Date expireTime = new Date(System.currentTimeMillis() + accessTokenTime);
        JwtBuilder jwtBuilder = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // (1)
                .setSubject("Valid")
                .setIssuer(dataMap.get("id")) // (2)
                .setIssuedAt(new Date())
                .setExpiration(expireTime) // 만료 시간은 properties에 설정
                .claim("id", dataMap.get("id"));// 비공개 클레임
        if(flag)
            jwtBuilder
                    .claim("access_Token", dataMap.get("access_Token"))// 액세스 토큰
                    .claim("refresh_Token", dataMap.get("refresh_Token")); // 리프레시 토큰

        return jwtBuilder.signWith(SignatureAlgorithm.HS256, secretKey).compact(); // 서명부분  deprecated 되어 추후 변경 필요 있다.
    }

    //로컬 refresh 토큰 생성후 반환
    private static String createRefreshToken(int refreshTokenTime, String access_Token) {

        Date expireTime = new Date(System.currentTimeMillis() + refreshTokenTime);

        // 추가로 클레임, 헤더 등 다양한 정보를 더 넣을 수 있음
        return Jwts.builder()
                .setSubject("valid")
                .setIssuedAt(new Date())                     // 발행일자를 담아서 암호화
                .signWith(SignatureAlgorithm.HS256,access_Token)
                .setExpiration(expireTime).compact();
    }

    //토큰 갱신 ( refresh Token 유효기간 좀 남아 있으면 refresh Token 은 갱신 안함 )
    //input : TokenMap(access_Token ,refresh_Token)
    public HashMap<String ,String> renewalToken(int accessTokenTime ,int refreshTokenTime ,
                                                String jwtsecretKey, HashMap<String ,String> tokenMap ,String db_refresh_Token){
        String access_Token = tokenMap.get("access_Token");
        String refresh_Token = tokenMap.get("refresh_Token");

        HashMap<String ,String> accessTokenMap =new HashMap<String,String>();
        accessTokenMap.put("access_Token",makeJwtToken(accessTokenTime,tokenMap,jwtsecretKey));

        //refresh_Token 유효하면 ,  access_Token 만 갱신
        if( isValidRefreshToken(tokenMap.get("refresh_Token"),db_refresh_Token) && isExpiredRefreshToken(tokenMap)  )
            return accessTokenMap;
        else
            return makeToken(accessTokenTime ,refreshTokenTime ,tokenMap , jwtsecretKey ); // access_Token ,refresh_Token 갱신

    }


    //로컬 access 토큰 검증   true: 유효 , false : 만료       , 만료 , 유효 뿐만 아니라 기타의 경우도 고려하긴 해야함 , 그리고 만료된것의 여부는 1시간 남았을 경우로 변경해야함
    private boolean isValidAccessToken(String jwtsecretKey,String access_Token){
        String tokenState = "expired";
        try {
            Date date = Jwts.parser()
                    .setSigningKey(jwtsecretKey)
                    .parseClaimsJws(access_Token)
                    .getBody()
                    .getExpiration();
        }catch(ExpiredJwtException e){
            log.info("만료된 토큰입니다");
            return false;
        }
        return true;
    }

    //로컬 refresh 토큰 DB 검증  , 유효하면 true , 무효하면 false
    private static boolean isValidRefreshToken(String client_refresh_Token,String db_refresh_Token) {
        if(client_refresh_Token==null || db_refresh_Token==null){
            log.info("client or DB 의 refresh_Token이 존재하지 않습니다.");
        }
        return db_refresh_Token.equals("client_refresh_Token");
    }

    //로컬 refresh 만료 검사  , 만료면 true, 유효하면 false
    //input : tokenMap
    private static boolean isExpiredRefreshToken(HashMap<String,String> tokenMap){ // 추후에 유효기간으로 변경할 필요 있다
        String access_Token = tokenMap.get("access_Token");
        String refresh_Token = tokenMap.get("refresh_Token");

        String tokenState = "expired";
        try {
            tokenState = Jwts.parser()
                    .setSigningKey(access_Token)
                    .parseClaimsJws(refresh_Token)
                    .getBody()
                    .getSubject();
        }catch(ExpiredJwtException e){
            log.info("만료된 토큰입니다");
        }

        return "expired".equals(tokenState)?true:false;

    }

    // authorizationHeader 로부터 HashMap 데이터 획득
    public static HashMap<String,Object> getClaimData(String authorizationHeader ,String secretKey ,String... keys) {

        HashMap<String,Object> dataMap = new HashMap<>();
        try {
            Claims claims = parseToken(authorizationHeader, secretKey);

            for (int idx = 0; idx < keys.length; idx++)
                dataMap.put(keys[idx], claims.get(keys[idx]));
        }catch(ExpiredJwtException e1){
            e1.printStackTrace();
            log.info("expired Token 입니다.");
            dataMap.put("state","expired");
        }catch(Exception e2){
            log.warn("토큰으로 부터 데이터 획득에 오류 발생");
            dataMap.put("state","invalid");
        }
        return dataMap;
    }

    private static Claims parseToken(String authorizationHeader,String secretKey) throws ExpiredJwtException {
        validationAuthorizationHeader(authorizationHeader); // (1)
        String token = extractAccessToken(authorizationHeader); // (2)

        Jwts.parser()
                .setSigningKey(secretKey) // (3)
                .parseClaimsJws(token) // (4)
                .getBody()
                .getSubject();
        return null;
    }

    private static void validationAuthorizationHeader(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            throw new IllegalArgumentException();
        }
    }

    private static String extractAccessToken(String authorizationHeader) {
        return authorizationHeader.substring("Bearer ".length());
    }

}

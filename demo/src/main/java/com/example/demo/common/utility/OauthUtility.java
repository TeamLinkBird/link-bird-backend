package com.example.demo.common.utility;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

@Slf4j
public class OauthUtility {

    public static HashMap<String ,String> checkSocialLogin
            (HashMap<String,String> socialTokenMap , HashMap<String,Long> timeMap ,
             HashMap<String,String> urlMap , HashMap<String,String> secretMap) throws Exception{

        Long shortTimeAccessToken = timeMap.get("shortTimeAccessToken");
        Long  accessTokenTime = timeMap.get("accessTokenTime");
        Long refreshTokenTime = timeMap.get("refreshTokenTime");
        String tokeninfo_kakao =  urlMap.get("tokeninfo_kakao");
        String jwtURL_kakao = urlMap.get("jwtURL_kakao");
        String userURL_kakao = urlMap.get("userURL_kakao");
        String clientID_kakao = secretMap.get("clientID_kakao");
        String client_secret_kakao = secretMap.get("client_secret_kakao");
        String secretKey = secretMap.get("jwtsecretKey");
        String refreshTokensecretKey = secretMap.get("refreshTokensecretKey");

        HashMap<String ,String> newSocialToken;
        HashMap<String ,String> newLocalToken;
        HashMap<String ,String> dataMap = new HashMap<>();

        Boolean isExpired = OauthUtility.isAccessTokenTimeShort (socialTokenMap.get("access_Token") ,tokeninfo_kakao ,shortTimeAccessToken); //accessToken 만료여부 검사
        String id;
        if(isExpired==null || isExpired) { //소셜 Access_Token 만료 됨
            newSocialToken = OauthUtility.renewalToken(jwtURL_kakao, socialTokenMap.get("refresh_Token"), clientID_kakao, client_secret_kakao);
            newLocalToken = JwtUtility.makeToken(accessTokenTime, refreshTokenTime, newSocialToken, secretKey, refreshTokensecretKey);
            id = OauthUtility.getUserId(newSocialToken.get("access_Token"),userURL_kakao);
            dataMap.put("social_access_Token",newSocialToken.get("social_access_Token"));
            dataMap.put("social_refresh_Token",newSocialToken.get("social_refresh_Token"));
            dataMap.put("access_Token",newLocalToken.get("access_Token"));
            dataMap.put("refresh_Token",newLocalToken.get("refresh_Token"));
        }
        else{//소셜 Access_Token 만료 안됨
            id = OauthUtility.getUserId(socialTokenMap.get("access_Token"),userURL_kakao);
        }
        dataMap.put("id",id);
        return dataMap;
    }

    //Social 토큰 획득
    public static HashMap<String, String> getToken(String authorize_code, String jwtURL, String clientID, String redirectURI, String client_secret) throws Exception {
        HashMap<String, String> map = new HashMap<>();


        //서버로 accesstoken 요청
        URL url = new URL(jwtURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //    POST 요청을 위해 기본값이 false인 setDoOutput을 true로
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        //    POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
        String sb = "grant_type=authorization_code" +
                "&client_id=" + clientID +  // fixed app id
                "&redirect_uri=" + redirectURI +    // redirect_uri
                "&code=" + authorize_code +
                "&client_secret=" + client_secret;
        bw.write(sb);
        bw.flush();

        //    결과 코드가 200이라면 성공
        int responseCode = conn.getResponseCode();
        log.info("responseCode : {}", responseCode);

        //    요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line = "";
        StringBuilder result = new StringBuilder();

        while ((line = br.readLine()) != null) {
            result.append(line);
        }
        log.info("response body : {}", result.toString());

        //    Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(result.toString());

        map.put("access_Token", element.getAsJsonObject().get("access_token").getAsString());
        map.put("refresh_Token", element.getAsJsonObject().get("refresh_token").getAsString());

        br.close();
        bw.close();

        return map;
    }

    //Social 토큰 갱신 ( refresh Token 유효기간 좀 남아 있으면 refresh Token 은 갱신 안함 )
    public static HashMap<String, String> renewalToken(String jwtURL, String refresh_Token, String clientID, String
            client_secret) throws Exception {
        HashMap<String, String> map = new HashMap<>();

        //서버로 accesstoken 요청
        URL url = new URL(jwtURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));

        String sb = "grant_type=refresh_token" +
                "&client_id=" + clientID +  // fixed app id
                "&refresh_token=" + refresh_Token +
                "&client_secret=" + client_secret;
        bw.write(sb);
        bw.flush();

        //    결과 코드가 200이라면 성공
        int responseCode = conn.getResponseCode();
        log.info("responseCode : {}", responseCode);

        //    요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line = "";
        StringBuilder result = new StringBuilder();

        while ((line = br.readLine()) != null) {
            result.append(line);
        }
        log.info("response body : {}", result.toString());

        //    Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(result.toString());

        //refresh 토큰 갱신 안되면 null 반환 ( 1개월 이상 남으면 노갱신 )
        JsonElement tmp = element.getAsJsonObject().get("refresh_token");
        refresh_Token = (tmp == null) ? refresh_Token : tmp.getAsString();


        map.put("access_Token", element.getAsJsonObject().get("access_token").getAsString());
        map.put("refresh_Token", refresh_Token);

        br.close();
        bw.close();
        return map;
    }

    //Social 유저 정보를 얻어옴
    public static String getUserId(String access_Token, String userURL) throws Exception {
        String id = null;
        URL url = new URL(userURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");

        //    요청에 필요한 Header에 포함될 내용
        conn.setRequestProperty("Authorization", "Bearer " + access_Token);

        int responseCode = conn.getResponseCode(); // IOException
        log.info("responseCode : {}", responseCode);

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        String line = "";
        StringBuilder result = new StringBuilder();

        while ((line = br.readLine()) != null) {
            result.append(line);
        }
        log.info("response body : {}", result);

        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(result.toString());

        long longId = element.getAsJsonObject().get("id").getAsLong();
        id = Long.toString(longId);


        return id;
    }

    //Social accessToken 유효기간이 짧은가 검사  ,  짧으면 true , 길면 false
    public static Boolean isAccessTokenTimeShort(String access_Token, String tokeninfokakao,
                                                 Long shortTimeAccessToken) throws Exception {
        // 요청하는 클라이언트마다 가진 정보가 다를 수 있기에 HashMap타입으로 선언
        boolean isRenewal;
        URL url = new URL(tokeninfokakao);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        //    요청에 필요한 Header에 포함될 내용
        conn.setRequestProperty("Authorization", "Bearer " + access_Token);

        int responseCode = conn.getResponseCode();
        log.info("responseCode : {}", responseCode);

        if (responseCode == 400) {
            log.info("카카오 플랫폼 서비스의 일시적 내부 장애 상태 이거나 액세스 토큰 정보가 옳바르지 않습니다.");
            return null;
        } else if (responseCode == 401) {
            log.info("유효하지 않은 앱키나 액세스 토큰으로 요청한 경우이거나 \n" +
                    "토큰 값이 잘못되었거나 만료되어 유효하지 않은 경우로 토큰 갱신 필요한 상태입니다.");
            return true;
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        String line = "";
        StringBuilder result = new StringBuilder();

        while ((line = br.readLine()) != null) {
            result.append(line);
        }
        log.info("response body : {}", result);

        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(result.toString());

        Long expires_in = element.getAsJsonObject().get("expires_in").getAsLong();
        log.info("shortTimeAccessToken : {}", shortTimeAccessToken);
        log.info("expires_in : {}", expires_in);

        isRenewal = expires_in <= shortTimeAccessToken; //true : 만료시간이 1시간도 안남아서 갱신해야 한다는 의미

        return isRenewal;
    }

    //Logout    사용자 id 반환( 다른 메소드에서 웹 서버 아이디 로그인 하는데 이용 )
    public static Long doLogout(String access_Token, String logoutURL) throws Exception {
        log.info("kakao 로그아웃 진행");
        long id;

        URL url = new URL(logoutURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");

        //    요청에 필요한 Header에 포함될 내용
        conn.setRequestProperty("Authorization", "Bearer " + access_Token);

        int responseCode = conn.getResponseCode();
        log.info("responseCode : {}", responseCode);

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        String line = "";
        StringBuilder result = new StringBuilder();

        while ((line = br.readLine()) != null) {
            result.append(line);
        }
        log.info("response body : {}", result);

        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(result.toString());

        id = element.getAsJsonObject().get("id").getAsLong();

        return id;
    }

    //비회원을 회원으로 전환   - db 사용 필요

    //db 에 소셜 회원 아이디 저장  - 컨트롤러에서 수행

    //소셜고유아이디ㅇ db에 존재하는지 확인  - db 사용 필요

}

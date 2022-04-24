package com.example.demo.login.service;

import com.example.demo.common.utility.JwtUtility;
import com.example.demo.common.utility.OauthUtility;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

@Slf4j
public class OauthLoginService {

    public static HashMap<String ,String> checkSocialToken
            (HashMap<String,String> socialTokenMap , HashMap<String,Integer> timeMap ,
             HashMap<String,String> urlMap , HashMap<String,String> secretMap) {

        int oauth_accessToken_Time = timeMap.get("oauth_accessToken_Time");
        int  accessTokenTime = timeMap.get("accessTokenTime");
        int refreshTokenTime = timeMap.get("refreshTokenTime");
        String tokeninfoURL =  urlMap.get("tokeninfoURL");
        String jwtURL = urlMap.get("jwtURL");
        String userURL = urlMap.get("userURL");
        String clientID = secretMap.get("clientID");
        String client_secret = secretMap.get("client_secret");
        String secretKey = secretMap.get("jwtsecretKey");

        HashMap<String ,String> newSocialToken;
        HashMap<String ,String> newLocalToken;
        HashMap<String ,String> dataMap = new HashMap<>();

        Boolean isExpired = OauthUtility.isAccessTokenTimeShort (socialTokenMap.get("access_Token") ,tokeninfoURL ,oauth_accessToken_Time); //accessToken 만료여부 검사
        if(isExpired) { //소셜 Access_Token 만료 됨
            newSocialToken = OauthUtility.renewalToken(jwtURL, socialTokenMap.get("refresh_Token"), clientID, client_secret);
            //소셜토큰이 만료되어서 .. 소셜 토큰을 갱신 하고 , 다시 로컬 토큰에 소셜토큰을 담고 ,
            // 소셜 토큰 정보를 통해 사용자의 id 를 뽑고 , 사용자에게 로컬토큰과 메인페이지 정보를 같이 전달 해주면 끝
            newLocalToken = JwtUtility.makeToken(accessTokenTime, refreshTokenTime, newSocialToken, secretKey);
            //사용자의 id 를 뽑고
            Object id = OauthUtility.getUserInfo(newSocialToken.get("access_Token"),userURL).get("id");
            //사용자의 id 에 대한 정보들을 뽑은 다음에 .. 일단 임시로 "1"
            dataMap.put("state","main");
            dataMap.put("link","1");
            dataMap.put("access_Token",newLocalToken.get("access_Token"));
            dataMap.put("refresh_Token",newLocalToken.get("refresh_Token"));
        }
        else{//소셜 Access_Token 만료 안됨
            //소셜 Acces_Token 으로 부터 사용자 id 를 뽑고 ,  id 에 해당하는 사용자에게 메인 페이지 정보를 전달해주면 끝.
            Object id = OauthUtility.getUserInfo(socialTokenMap.get("access_Token"),userURL).get("id");
            //사용자의 id 에 대한 정보들을 뽑은 다음에 .. 일단 임시로 "1"
            dataMap.put("state","main");
            dataMap.put("link","1");
        }
        return dataMap;
    }
}

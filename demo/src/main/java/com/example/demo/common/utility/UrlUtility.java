package com.example.demo.common.utility;

import com.google.gson.JsonObject;

import java.util.HashMap;

public class UrlUtility {


    public static HashMap<String, String> loginUrl(String serverURI) {

        JsonObject jo = new JsonObject();

        HashMap<String,String> urlMap = new HashMap<>();

        urlMap.put("state","login");
        urlMap.put("kakao",serverURI+"/kakaoLogin");
        urlMap.put("unsigned",serverURI+"/unsignedLogin");

        return urlMap;
    }
}

package com.example.demo.interceptor;

import com.example.demo.common.utility.JwtUtility;
import com.example.demo.common.utility.OauthUtility;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Value("${jwtsecretKey}")
    String jwtsecretKey;

    @Value("${userURL.kakao}")
    String userURL;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String data = JwtUtility.extractAccessToken(authorizationHeader);
        if(data==null){
            response.sendRedirect("/login");
            return false;
        }
        HashMap<String,String> dataMap = JwtUtility.getSocialToken(request,jwtsecretKey);
        String id = dataMap.get("id");
        if(id == null) { // social 의 경우
            id = OauthUtility.getUserId(dataMap.get("access_Token"), userURL);
        }
        request.setAttribute("id",id);
        return true;
    }

}

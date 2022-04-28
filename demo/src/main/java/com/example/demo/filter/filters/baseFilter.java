package com.example.demo.filter.filters;

import com.example.demo.common.utility.JwtUtility;
import com.example.demo.common.utility.OauthUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

import static com.example.demo.login.service.LoginService.checkLocalToken;

@Slf4j
public class baseFilter implements Filter {

    String secretKey = "wfonjqwevowhvwefewfdsf";  // 이렇게 넣기 싫은데..
    String userURL = "https://kapi.kakao.com/v2/user/me"; // 이렇게 넣기 싫은데..

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("baseFilter 생성");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String authorizationHeader = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
        String data = JwtUtility.extractAccessToken(authorizationHeader);
        if(data==null){
            httpResponse.sendRedirect("/login");
            return;
        }
        String userId = getId(httpRequest ,secretKey ,userURL);
        request.setAttribute("id",userId);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        log.info("baseFilter 종료");
    }

    //Test
    //input : HttpServletRequest , jwtSecretKey( application.properties 에 존재 ) , userURL ( application.properties 에 존재 )
    public  static String getId(HttpServletRequest request ,String secretKey ,String userURL){
        HashMap<String,String> dataMap = checkLocalToken(request,secretKey);
        String userId = dataMap.get("id");
        String social_access_Token = dataMap.get("access_Token");
        if(userId==null) { //소셜 접속 일 경우 , 소셜 id 반환
            HashMap<String ,Object> tmpMap = OauthUtility.getUserInfo(social_access_Token, userURL);
            Object tmpId = tmpMap.get("id");
            return ((Long)tmpId).toString();
        }
        else // 비회원 접속 일 경우 , 비회원 id 반환
            return userId;
    }
}

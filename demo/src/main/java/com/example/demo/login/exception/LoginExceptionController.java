package com.example.demo.login.exception;

import com.example.demo.common.utility.UrlUtility;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;

@Slf4j
@RestControllerAdvice
public class LoginExceptionController {

    @Value("${serverURI}")
    String serverURI;

    @ExceptionHandler(value = Exception.class)
    public HashMap<String, String> exception(HttpServletRequest request, Exception ex) {
        log.info("ExceptionHandler 수행");
        HashMap<String, String> dataMap = new HashMap<>();
        String requestURI = request.getRequestURI();
        //옳바르지 않은 AccessToken , jwt 예외 case
        if (ex instanceof IllegalArgumentException ||
                ex instanceof IndexOutOfBoundsException ||
                ex instanceof NullPointerException ||
                ex instanceof UnsupportedJwtException ||
                ex instanceof MalformedJwtException ||
                ex instanceof SignatureException)
        {
            ex.printStackTrace();
            return UrlUtility.loginUrl(serverURI);
        }
        else if(ex instanceof ExpiredJwtException && "refresh_Token expired".equals(ex.getMessage())){ // 서버 refresh_Token 만료됬을 경우
            return UrlUtility.loginUrl(serverURI);
        }
        else if (ex instanceof ExpiredJwtException) {// 서버 access_Token 만료됬을 경우
            log.info("ExpiredJwtException");
            dataMap.put("state", "refresh_Token");
            dataMap.put("url", serverURI + "/refresh_Token");
            dataMap.put("source", requestURI);
            dataMap.put("method", "get");
            log.info("사용자에게 전달하는 데이터 : {}", dataMap);
            return dataMap;
        }
        //////////////////OauthUtility.getUserId , OauthUtility.getToken , OauthUtility.isAccessTokenTimeShort , OauthUtility.renewalToken ,///////////////////
        //////////////////OauthUtility.doLogout
        else if (ex instanceof IOException) {
            log.warn("IOException");
            log.warn(ex.getMessage());
        }

        //////////////////LoginException//////////////////
        else if (ex instanceof LoginException) {
            log.info("LoginException");
            ex.printStackTrace();
            return UrlUtility.loginUrl(serverURI);
        }

        //////////////////All Exception///////////////////
        else if (ex instanceof Exception) {
            log.warn("오류 발생, 관리자에게 문의하십시오.");
            ex.printStackTrace();
        }


        return UrlUtility.loginUrl(serverURI); // 대부분 에러의 경우 로그인 페이지로
    }

}

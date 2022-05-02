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

import java.io.IOException;
import java.util.HashMap;

@Slf4j
@RestControllerAdvice
public class LoginExceptionController {

    @Value("${serverURI}")
    String serverURI;

    @ExceptionHandler(value = Exception.class)
    public HashMap<String, String> exception(Exception ex){
        log.info("ExceptionHandler 수행");
        log.info("serverURI : {}",serverURI);
        //////////////////OauthUtility.getSocialToken///////////////////
        //옳바르지 않은 AccessToken
        if(ex instanceof IllegalArgumentException){
            log.warn("IllegalArgumentException");
            log.warn("클라이언트로 부터 넘어온 access_Token이 옳바르지 않습니다.");
        }else if(ex instanceof IndexOutOfBoundsException){
            log.warn("IndexOutOfBoundsException");
            log.warn(ex.getMessage());
        }else if(ex instanceof NullPointerException){
            log.warn("NullPointerException");
            log.warn(ex.getMessage());
        }

        //JWT 예외
        else if(ex instanceof UnsupportedJwtException){
            log.info("UnsupportedJwtException");
            log.info(ex.getMessage());
        }else if(ex instanceof MalformedJwtException){
            log.info("MalformedJwtException");
            log.info(ex.getMessage());
        }else if(ex instanceof SignatureException){
            log.info("SignatureException");
            log.info(ex.getMessage());
        }else if(ex instanceof ExpiredJwtException){  // 서버 access_Token의  만료인 경우
            log.info("ExpiredJwtException");
            log.info(ex.getMessage());
            //response.sendRedirect("/refresh_Token");
        }else if(ex instanceof IllegalArgumentException){
            log.info("IllegalArgumentException");
            log.info(ex.getMessage());
        }
        //////////////////OauthUtility.getUserId , OauthUtility.getToken , OauthUtility.isAccessTokenTimeShort , OauthUtility.renewalToken ,///////////////////
        //////////////////OauthUtility.doLogout
        else if(ex instanceof IOException) {
            log.warn("IOException");
            log.warn(ex.getMessage());
        }


        //////////////////All Exception///////////////////
        else if(ex instanceof Exception){
            log.warn("오류 발생, 관리자에게 문의하십시오.");
        }
        return UrlUtility.loginUrl(serverURI); // 대부분 에러의 경우 로그인 페이지로
    }

}

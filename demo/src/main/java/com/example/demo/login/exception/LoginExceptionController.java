package com.example.demo.login.exception;

import com.example.demo.login.controller.LoginController;
import com.example.demo.login.controller.RefreshTokenController;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;

@Slf4j
@RestControllerAdvice(assignableTypes = {LoginController.class, RefreshTokenController.class})
public class LoginExceptionController {

    @Value("${serverURI}")
    String serverURI;

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<HashMap<String,String>> exception(HttpServletRequest request, Exception ex) {
        log.info("ExceptionHandler 수행");
        HashMap<String, String> errorMap = new HashMap<>();
        //옳바르지 않은 AccessToken , jwt 예외 case
        if (ex instanceof IllegalArgumentException ||
                ex instanceof IndexOutOfBoundsException ||
                ex instanceof NullPointerException ||
                ex instanceof UnsupportedJwtException ||
                ex instanceof MalformedJwtException ||
                ex instanceof SignatureException){
            errorMap.put("error","This is bad access_Token of server");
        }
        else if(ex instanceof ExpiredJwtException && "refresh_Token expired".equals(ex.getMessage())){ // 서버 refresh_Token 만료됬을 경우
            errorMap.put("error","This is expired refresh_Token of server");
        }
        else if (ex instanceof ExpiredJwtException) {// 서버 access_Token 만료됬을 경우
            log.info("만료 시간 : {}",((ExpiredJwtException) ex).getClaims().getExpiration());
            errorMap.put("error","This is expired access_Token of server");
            return new ResponseEntity(errorMap,HttpStatus.UNAUTHORIZED);
        }
        //////////////////OauthUtility.getUserId , OauthUtility.getToken , OauthUtility.isAccessTokenTimeShort , OauthUtility.renewalToken ,///////////////////
        //////////////////OauthUtility.doLogout
        else if (ex instanceof IOException) {
            errorMap.put("error","IOException error occured!");
        }

        //////////////////LoginException//////////////////
        else if (ex instanceof LoginException) {
            errorMap.put("error","social refresh_Token의 만료이거나 그 외의 오류로 인하여 로그인 페이지로 이동합니다.");
        }

        //////////////////All Exception///////////////////
        else if (ex != null) {
            errorMap.put("error","오류 발생, 서버 관리자에게 문의하십시오.");
        }
        ex.printStackTrace();
        return new ResponseEntity(errorMap,HttpStatus.NOT_ACCEPTABLE);
    }
}

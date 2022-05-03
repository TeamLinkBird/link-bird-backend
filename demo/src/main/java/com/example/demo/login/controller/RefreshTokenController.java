package com.example.demo.login.controller;

import com.example.demo.common.utility.JwtUtility;
import com.example.demo.login.entity.User;
import com.example.demo.login.exception.LoginException;
import com.example.demo.login.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@Controller
public class RefreshTokenController {

    @PersistenceContext
    EntityManager em;

    @Autowired
    LoginService loginService;

    @Value("${accessTokenTime}")
    Long accessTokenTime;

    @Value("${refreshTokenTime}")
    Long refreshTokenTime;

    @Value("forwardsecretKey")
    String forwardsecretKey;

    @Value("${jwtsecretKey}")
    String jwtsecretKey;

    @Value("refreshTokensecretKey")
    String refreshTokensecretKey;



    @Transactional
    @GetMapping("/refresh_Token")
    public String check_Server_Refresh_Token(HttpServletRequest request, @RequestBody HashMap<String, String> tokenMap) throws Exception {
        String forwardUrl = tokenMap.get("source");
        String header = request.getHeader("Authorization");
        JwtUtility.validationAuthorizationHeader(header);
        String access_Token = JwtUtility.extractAccessToken(header);
        tokenMap.put("access_Token", access_Token);

        //받은 refresh_Token 검색
        User user = loginService.findByRefreshToken(tokenMap.get("refresh_Token"));
        if (user == null) {
            throw new LoginException();
        }

        String id = user.getUserId();
        Boolean isExpired = JwtUtility.isExpiredRefreshToken(tokenMap);
        if(!isExpired) {
            //1.서버 refresh_Token 유효할 경우 ,
            ////1-1.서버 refresh_Token 으로 부터 claim datas(소셜 access_Token, 소셜 refresh_Token)을 얻는다.
            ////1-2. 얻은 정보를 토대로 서버 access_Token을 만든다.
            ////1-3. 서버에게 id , 서버 access_Token , 서버 refresh_Token 반환.
            HashMap <String, String> social_Token = JwtUtility.getClaimDataFromRefreshToken(tokenMap.get("access_Token"),tokenMap.get("refresh_Token"),refreshTokensecretKey);
            String new_access_Token = JwtUtility.makeJwtToken(accessTokenTime, social_Token, jwtsecretKey);
            request.setAttribute("id", id);
            request.setAttribute("access_Token", new_access_Token);  // 서버 access_Token
            request.setAttribute("forwardsecretKey", forwardsecretKey);
            return "forward:/" + forwardUrl;
        }
        else {
            //2.서버 refresh_Token 무효할 경우
            ////2-1. db에서 서버 refresh_Token을 null 로 만들고
            ////2-2. 사용자에게 로그인 페이지 전달.
            user.setRefreshToken(null);
            em.persist(user);
            throw new LoginException();
        }
    }

}

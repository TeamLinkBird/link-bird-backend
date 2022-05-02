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

    @Transactional
    @GetMapping("/refresh_Token")
    public String check_Server_Refresh_Token(HttpServletRequest request, @RequestBody HashMap<String, String> tokenMap) throws Exception {
        String forwardUrl = tokenMap.get("source");
        String header = request.getHeader("Authorization");
        String access_Token;
        JwtUtility.validationAuthorizationHeader(header);
        access_Token = JwtUtility.extractAccessToken(header);
        tokenMap.put("access_Token", access_Token);

        //받은 refresh_Token 검색
        User user = loginService.findByRefreshToken(tokenMap.get("refresh_Token"));
        if (user == null) {
            throw new LoginException();
        }


        //서버 refresh_Token 유효,무효 둘다
        String id = user.getUserId();
        String social_access_Token = user.getSocialAccessToken();
        String social_refresh_Token = user.getSocialRefreshToken();
        HashMap<String, String> token = new HashMap<>();
        token.put("id", id);
        token.put("access_Token", social_access_Token);
        token.put("refresh_Token", social_refresh_Token);
        token = JwtUtility.makeToken(accessTokenTime, refreshTokenTime, token, jwtsecretKey); //서버 Token 갱신
        user.setRefreshToken(token.get("refresh_Token"));
        em.persist(user);
        request.setAttribute("id", id);
        request.setAttribute("access_Token", token.get("access_Token"));  // 서버 access_Token
        request.setAttribute("refresh_Token", token.get("refresh_Token")); // 서버 refresh_Token
        request.setAttribute("forwardsecretKey", forwardsecretKey);
        return "forward:/" + forwardUrl;

    }

}

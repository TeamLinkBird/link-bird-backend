package com.example.demo.interceptor;

import com.example.demo.common.utility.JwtUtility;
import com.example.demo.common.utility.OauthUtility;
import com.example.demo.login.entity.User;
import com.example.demo.login.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @PersistenceContext
    EntityManager em;

    @Autowired
    LoginService loginService;

    @Value("${accessTokenTime}")
    Long accessTokenTime;

    @Value("${refreshTokenTime}")
    Long refreshTokenTime;

    @Value("${shortTimeAccessToken}")
    Long shortTimeAccessToken;

    //oauth_refreshToken_Time 은 social 에서 정해져 있다.

    @Value("${serverURI}")
    String serverURI;

    @Value("${tokeninfoURL.kakao}")
    String tokeninfoURL;

    @Value("${jwtURL.kakao}")
    String jwtURL;

    @Value("${userURL.kakao}")
    String userURL;

    @Value(("${logoutURL.kakao}"))
    String kakaologoutURL;

    @Value("${clientID.kakao}")
    String clientID;

    @Value("${clientSECRET.kakao}")
    String client_secret;

    @Value("${jwtsecretKey}")
    String jwtsecretKey;

    @Value("forwardsecretKey")
    String forwardsecretKey;

    @Value("refreshTokensecretKey")
    String refreshTokensecretKey;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String pw = (String)request.getAttribute("forwardsecretKey");
        if(forwardsecretKey.equals(pw))
            return true;
        HashMap<String,String> dataMap = JwtUtility.getSocialToken(request,jwtsecretKey);
        String id = dataMap.get("id");
        if(id == null) { // social 의 경우
            HashMap<String ,Long> timeMap = new HashMap<>();
            HashMap<String ,String> urlMap = new HashMap<>();
            HashMap<String ,String> secretMap = new HashMap<>();
            makeMap(timeMap, urlMap, secretMap);
            HashMap<String ,String> socialToken;
            socialToken = OauthUtility.checkSocialLogin(dataMap , timeMap ,urlMap , secretMap);
            id = getSocialId(socialToken);
        }
        request.setAttribute("id",id);
        return true;
    }

    @Transactional
    public String getSocialId(HashMap<String, String> socialToken){
        String acesss_Token = socialToken.get("acesss_Token");
        if(acesss_Token != null) {
            String refresh_Token = socialToken.get("refresh_Token");
            String id = socialToken.get("id");
            User user = loginService.findByuserId(id);
            user.setRefreshToken(refresh_Token);
            em.persist(user);
        }
        return socialToken.get("id");
    }

    public void makeMap(HashMap<String, Long> timeMap, HashMap<String, String> urlMap, HashMap<String, String> secretMap) {
        timeMap.put("accessTokenTime", accessTokenTime);
        timeMap.put("refreshTokenTime", refreshTokenTime);
        timeMap.put("shorTimeAccessToken", shortTimeAccessToken);
        urlMap.put("serverURI", serverURI);
        urlMap.put("tokeninfoURL", tokeninfoURL);
        urlMap.put("jwtURL", jwtURL);
        urlMap.put("userURL", userURL);
        secretMap.put("clientID", clientID);
        secretMap.put("client_secret", client_secret);
        secretMap.put("jwtsecretKey", jwtsecretKey);
        secretMap.put("refreshTokensecretKey",refreshTokensecretKey);
    }
}

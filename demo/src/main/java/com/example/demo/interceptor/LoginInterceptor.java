package com.example.demo.interceptor;

import com.example.demo.common.utility.JwtUtility;
import com.example.demo.common.utility.OauthUtility;
import com.example.demo.login.entity.User;
import com.example.demo.login.exception.AccessUrlException;
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
import java.util.Map;

@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @PersistenceContext
    EntityManager em;

    @Autowired
    LoginService loginService;

    @Value("#{${socialUrlMap}}")
    Map<String,String> socialUrlMap;

    @Value("#{${secretMap}}")
    Map<String,String> secretMap;

    @Value("#{${timeMap}}")
    Map<String,Long> timeMap;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("request uri : {} ",request.getRequestURI());
        if(hasExcludeUrl(request.getRequestURI()))
            return false;
        // 1 Depth -> 2 Depth
        HashMap<String,String> dataMap = JwtUtility.getSocialToken(request,secretMap.get("jwtsecretKey"));
        String id = dataMap.get("id");
        log.info("id: {}",id);
        if(id == null) { // social 의 경우
            HashMap<String ,String> token;
            token = OauthUtility.checkSocialLogin(dataMap , timeMap ,socialUrlMap , secretMap);
            id = getSocialId(request, token);
        }
        request.setAttribute("id",id);
        return true;
    }

    private boolean hasExcludeUrl(String uri) {
        return "/favicon.ico".equals(uri);
    }

    @Transactional
    public String getSocialId(HttpServletRequest request , HashMap<String, String> token) throws Exception{
        String acesss_Token = token.get("acesss_Token");
        if(acesss_Token != null) {//Token 에 access_Token 이 null이 아니라면 , 사용자에게 서버 access_Token , 서버 refresh_Token 전달하며 끝낸다.
            String refresh_Token = token.get("refresh_Token");
            String id = token.get("id");
            User user = loginService.findByuserId(id);
            user.setRefreshToken(refresh_Token);
            em.persist(user);
            request.setAttribute("access_Token",acesss_Token);
            request.setAttribute("refresh_Token",refresh_Token);
            throw new AccessUrlException("서버 access_Token , 서버 refresh_Token 을 갱신하여 사용자에게 전달합니다."); // http status 200 반환
        }
        return token.get("id");
    }
}

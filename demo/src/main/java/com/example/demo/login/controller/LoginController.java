package com.example.demo.login.controller;

import com.example.demo.common.commonenum.Auth;
import com.example.demo.common.commonenum.Social;
import com.example.demo.common.commonenum.UserStatus;
import com.example.demo.common.utility.JwtUtility;
import com.example.demo.common.utility.OauthUtility;
import com.example.demo.common.utility.UrlUtility;
import com.example.demo.login.entity.User;
import com.example.demo.login.service.LoginService;
import com.example.demo.login.service.OauthLoginService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@Slf4j
@RestController
public class LoginController {

    //private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpaLogin");
    @PersistenceContext
    EntityManager em;

    @Autowired
    LoginService loginService;

    @Value("${accessTokenTime}")
    int accessTokenTime;

    @Value("${refreshTokenTime}")
    int refreshTokenTime;

    @Value("${accessTokenTime.oauth}")
    int oauth_accessToken_Time;

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


    @GetMapping("/")
    public HashMap<String, String> getMainData(HttpServletRequest request) {
        HashMap<String, String> idMap = new HashMap<>();
        idMap.put("id", (String)request.getAttribute("id"));
        return idMap;
    }

    @GetMapping("/login")
    public HashMap<String, String> login() {
        return UrlUtility.loginUrl(serverURI);
    }

    @Transactional
    @GetMapping("/login/unsigned")
    public HashMap<String, String> unsignedLogin(@RequestBody HashMap<String, String> idMap) throws Exception{
        //일단 클라이언트에서 보낸 서버토큰은 확실히 없다.

        HashMap<String, String> dataMap = new HashMap<>();
        String userId = idMap.get("id");
        log.info("단말기 ID : {}", userId);
        if (userId == null) {
            throw new NullPointerException("단말기에서 전송한 ID 데이터가 없습니다.");
        }

        dataMap.put("id", userId);
        dataMap = JwtUtility.makeToken(accessTokenTime, refreshTokenTime, dataMap, jwtsecretKey);

        User user = loginService.findAByuserId(userId);

        if (user != null) {
            log.info("DB 에 단말기 정보 존재 : {}", userId);
            user.setRefreshToken(dataMap.get("refresh_Token"));
        }
        else {
            log.info("DB 에 단말기 정보 존재 X : {}", userId);
            user = new User();
            user.setUserId(userId);
            user.setAuth(Auth.비회원);
            user.setSocial(Social.없음);
            user.setUserStatus(UserStatus.활성화);
            user.setRefreshToken(dataMap.get("refresh_Token"));
        }
        em.persist(user);
        dataMap.remove("id");
        dataMap.put("state", "main");
        return dataMap;
    }

    @Transactional
    @GetMapping("/login/kakao")
    public HashMap<String, String> kakaoLogin(HttpServletRequest request) throws Exception {
        HashMap<String, String> token = null;
        HashMap<String, String> serverToken = null;
        String userId = null;
        User user = null;

        String authorize_code = request.getParameter("code");
        token = OauthUtility.getToken(authorize_code, jwtURL, clientID, request.getRequestURL().toString(), client_secret);
        userId = OauthUtility.getUserId(token.get("access_Token"), userURL);
        user = loginService.findAByuserId(userId);
        token = JwtUtility.makeToken(accessTokenTime, refreshTokenTime, token, jwtsecretKey);
        serverToken = token;

        // CASE 1) DB 에 회원 아이디가 있다면 , 회원 아이디에 대한 메인페이지 정보와 로컬 토큰을 사용자에게 반환한다.
        // CASE 2) DB 에 회원 아이디가 없다면 , 회원 아이디를 등록하고 ,  메인페이지 정보와 로컬 토큰을 사용자에게 반환한다.
        if (user == null) {
            log.info("새롭게 계정 등록");
            user = new User();
            user.setUserId(userId);
            user.setAuth(Auth.소셜회원);
            user.setSocial(Social.카카오);
            user.setUserStatus(UserStatus.활성화);
        }
        user.setRefreshToken(serverToken.get("refresh_Token"));
        em.persist(user);
        serverToken.put("state", "main");
        return serverToken;
    }

    @Transactional
    @PostMapping("/logout")
    public HashMap<String, String> logout(@RequestBody HashMap<String, String> dataMap, HttpServletRequest request) throws Exception {

        HashMap<String, String> tokenMap = null;
        String header = request.getHeader("Authorization");

        try {  // 로컬 Access_Token 이 만료 되었을 경우, 로그인 페이지 안내
            tokenMap = JwtUtility.getClaimData(header, jwtsecretKey, "access_Token", "refresh_Token", "id");
        }catch(ExpiredJwtException e){
            log.info("서버의 access_Token 이 만료 되었으므로 login 페이지정보 전달");
            return UrlUtility.loginUrl(serverURI);
        }

        String userId = tokenMap.get("id"); // 단말기 id 가져옴
        User user = null;
        if (userId == null) {//social 로그인 상태
            if (OauthUtility.isAccessTokenTimeShort(tokenMap.get("access_Token"), tokeninfoURL, oauth_accessToken_Time)) //소셜 토큰 무효하면 갱신.
                tokenMap = OauthUtility.renewalToken(jwtURL, tokenMap.get("refresh_Token"), clientID, client_secret);
            //소셜 토큰을 만료시킨다
            userId = OauthUtility.doLogout(tokenMap.get("access_Token"), kakaologoutURL).toString();
        }
        user = loginService.findAByuserId(userId);
        if(user == null) {
            log.warn("로그아웃 도중 db 로부터 사용자 ID 검색에 실패 하였습니다.");
        }else{
            user.setRefreshToken(null); // 서버 refresh_Token 을 db 에서 제거
            em.persist(user);
        }

        return UrlUtility.loginUrl(serverURI);
    }

    public void makeMap(HashMap<String, Integer> timeMap, HashMap<String, String> urlMap, HashMap<String, String> secretMap) {
        timeMap.put("accessTokenTime", accessTokenTime);
        timeMap.put("refreshTokenTime", refreshTokenTime);
        timeMap.put("oauth_accessToken_Time", oauth_accessToken_Time);
        urlMap.put("serverURI", serverURI);
        urlMap.put("tokeninfoURL", tokeninfoURL);
        urlMap.put("jwtURL", jwtURL);
        urlMap.put("userURL", userURL);
        secretMap.put("clientID", clientID);
        secretMap.put("client_secret", client_secret);
        secretMap.put("jwtsecretKey", jwtsecretKey);
    }
}

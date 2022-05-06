package com.example.demo.login.controller;

import com.example.demo.common.commonenum.Auth;
import com.example.demo.common.commonenum.Social;
import com.example.demo.common.commonenum.UserStatus;
import com.example.demo.common.utility.JwtUtility;
import com.example.demo.common.utility.OauthUtility;
import com.example.demo.login.entity.User;
import com.example.demo.login.exception.LoginException;
import com.example.demo.login.service.LoginService;
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

    @Value("${serverURI}")
    String serverURI;

    @Value("${tokeninfo.kakao}")
    String tokeninfo_kakao;

    @Value("${jwtURL.kakao}")
    String jwtURL_kakao;

    @Value("${userURL.kakao}")
    String userURL_kakao;

    @Value(("${logoutURL.kakao}"))
    String logoutURL_kakao;

    @Value("${clientID.kakao}")
    String clientID_kakao;

    @Value("${clientSECRET.kakao}")
    String client_secret_kakao;

    @Value("${jwtsecretKey}")
    String jwtsecretKey;

    @Value("refreshTokensecretKey")
    String refreshTokensecretKey;


    @GetMapping("/")
    public String getDMainata(HttpServletRequest request) {
        log.info("id : {}", request.getAttribute("id"));
        return "ok";
    }

    @GetMapping("/login")
    public HashMap<String, String> login() throws Exception{
        throw new LoginException();
    }

    @Transactional
    @GetMapping("/login/unsigned")
    public HashMap<String, String> unsignedLogin(@RequestBody HashMap<String, String> idMap){
        //일단 클라이언트에서 보낸 서버토큰은 확실히 없다.

        HashMap<String, String> dataMap = new HashMap<>();
        String userId = idMap.get("id");
        if (userId == null) {
            throw new NullPointerException("단말기에서 전송한 ID 데이터가 없습니다.");
        }

        dataMap.put("id", userId);
        dataMap = JwtUtility.makeToken(accessTokenTime, refreshTokenTime, dataMap, jwtsecretKey, refreshTokensecretKey);
        User user = loginService.findByuserId(userId);

        if (user != null) {
            log.info("DB 에 단말기 정보 존재 : {}", userId);
        }
        else {
            log.info("DB 에 단말기 정보 존재 X : {}", userId);
            user = new User();
            user.setUserId(userId);
            user.setAuth(Auth.비회원);
            user.setSocial(Social.없음);
            user.setUserStatus(UserStatus.활성화);
        }
        user.setRefreshToken(dataMap.get("refresh_Token"));
        em.persist(user);
        return dataMap;
    }

    @Transactional
    @GetMapping("/login/kakao")
    public HashMap<String, String> kakaoLogin(HttpServletRequest request) throws Exception {
        HashMap<String, String> token;
        HashMap<String, String> serverToken;
        HashMap<String, String> socialToken;
        String userId;
        User user;

        String authorize_code = request.getParameter("code");
        log.info("authorize_code : {}",authorize_code);
        token = OauthUtility.getToken(authorize_code, jwtURL_kakao, clientID_kakao, request.getRequestURL().toString(), client_secret_kakao);
        log.info("소셜 token : {}",token);
        socialToken = new HashMap<>(token);
        userId = OauthUtility.getUserId(socialToken.get("access_Token"), userURL_kakao);
        user = loginService.findByuserId(userId);
        log.info("소셜 socialToken : {}",socialToken);
        serverToken = JwtUtility.makeToken(accessTokenTime, refreshTokenTime, socialToken, jwtsecretKey, refreshTokensecretKey);

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
        log.info("refresh_Token : {}",serverToken.get("refresh_Token"));
        em.persist(user);
        return serverToken;
    }

    @Transactional
    @GetMapping("/login/naver")
    public HashMap<String, String> naverLogin() {
        log.info("ok");
        return null;
    }

    @Transactional
    @PostMapping("/logout")
    public HashMap<String, String> logout(HttpServletRequest request) throws Exception {

        HashMap<String, String> tokenMap;
        String header = request.getHeader("Authorization");

        try {  // 로컬 Access_Token 이 만료 되었을 경우, 로그인 페이지 안내
            tokenMap = JwtUtility.getClaimData(header, jwtsecretKey, "access_Token", "refresh_Token", "id");
        }catch(ExpiredJwtException e){
            log.info("서버의 access_Token 이 만료 되었으므로 login 페이지정보 전달");
            throw new LoginException();
        }

        String userId = tokenMap.get("id"); // 단말기 id 가져옴
        User user;
        if (userId == null) {//social 로그인 상태
            Boolean isRenewal = OauthUtility.isAccessTokenTimeShort(tokenMap.get("access_Token"), tokeninfo_kakao, shortTimeAccessToken);
            if (isRenewal==null || isRenewal) //소셜 토큰 무효하면 갱신.
                tokenMap = OauthUtility.renewalToken(jwtURL_kakao, tokenMap.get("refresh_Token"), clientID_kakao, client_secret_kakao);
            //소셜 토큰을 만료시킨다
            userId = OauthUtility.doLogout(tokenMap.get("access_Token"), logoutURL_kakao).toString();
        }
        user = loginService.findByuserId(userId);
        if(user == null) {
            log.warn("로그아웃 도중 db 로부터 사용자 ID 검색에 실패 하였습니다.");
        }else{
            user.setRefreshToken(null);
            em.persist(user);
        }
        throw new LoginException();
    }

    @Transactional
    @GetMapping("/refresh_Token")
    public HashMap<String, String> check_Server_Refresh_Token(@RequestBody HashMap<String, String> tokenMap) throws Exception {
        //받은 refresh_Token 검색
        User user = loginService.findByRefreshToken(tokenMap.get("refresh_Token"));
        if (user == null) {
            throw new LoginException();
        }

        boolean isExpired = JwtUtility.isExpiredRefreshToken(tokenMap.get("refresh_Token"),refreshTokensecretKey);
        if(!isExpired) {
            //1.서버 refresh_Token 유효할 경우 ,
            ////1-1.서버 refresh_Token 으로 부터 claim datas(소셜 access_Token, 소셜 refresh_Token)을 얻는다.
            ////1-2. 얻은 정보를 토대로 서버 access_Token을 만든다.
            ////1-3. 서버에게 id , 서버 access_Token , 서버 refresh_Token 반환.
            HashMap <String, String> social_Token = JwtUtility.getClaimDataFromRefreshToken(tokenMap.get("refresh_Token"),refreshTokensecretKey);
            if(user.getAuth().equals(Auth.비회원)) {//비회원이라면  social_Token에 key : id , value 추가
                social_Token.put("id",user.getUserId());
            }
            String new_access_Token = JwtUtility.makeJwtToken(accessTokenTime, social_Token, jwtsecretKey);
            tokenMap.clear();
            tokenMap.put("access_Token",new_access_Token);
            return tokenMap;
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

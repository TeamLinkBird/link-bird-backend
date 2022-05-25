package com.example.demo.login.controller;

import com.example.demo.common.commonenum.Auth;
import com.example.demo.common.commonenum.Social;
import com.example.demo.common.commonenum.UserStatus;
import com.example.demo.common.utility.JwtUtility;
import com.example.demo.common.utility.OauthUtility;
import com.example.demo.login.dto.SocialToken;
import com.example.demo.login.entity.User;
import com.example.demo.login.exception.LoginException;
import com.example.demo.login.service.LoginService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "login", description = "Login 관련 APi")
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

    @Value("#{${socialUrlMap}}")
    Map<String,String> socialUrlMap;

    //단말기 id 로 로그인
    /*
    * input : idMap ( id )
    * output : dataMap ( 서버 access_Token , 서버 refresh_Token )
    * */
    @Operation(tags = "login", summary = "비회원 로그인", description = "")
    @Transactional
    @PostMapping("/login/unsigned")
    public HashMap<String, String> unsignedLogin(@RequestBody HashMap<String, String> idMap){
        //디도스 방지로 중복 ip 시간 제한 두어야함

        HashMap<String, String> dataMap = new HashMap<>();
        String userId = idMap.get("id");
        if (userId == null) {
            throw new LoginException("단말기에서 전송한 ID 데이터가 없습니다.");
        }

        dataMap.put("id", userId);
        dataMap = JwtUtility.makeToken(accessTokenTime, refreshTokenTime, dataMap, jwtsecretKey, refreshTokensecretKey);
        User user = loginService.findByuserId(userId);

        if (user == null) {
            log.info("DB 에 단말기 정보 존재 X : {}", userId);
            user = new User();
            user.setUserId(userId);
            user.setAuth(Auth.비회원);
            user.setSocial(Social.없음);
            user.setUserStatus(UserStatus.활성화);
        }
        user.setRefreshToken(dataMap.get("refresh_Token"));
        em.persist(user);
        dataMap.remove("id");
        return dataMap;
    }

    // 소셜 로그인을 하는 사용자에게 서버 토큰을 반영
    /*
     * input : idToken
     * output : idToken
     * */
    @Operation(tags = "login", summary = "구글 로그인", description = "")
    @Transactional
    @PostMapping("/login/google")
    public HashMap<String, String> googleLogin(@RequestBody HashMap<String,String> idTokenMap) throws Exception {
        HashMap<String, String> serverToken;
        Social social_enum;
        String userId;
        User user;
        String getUserUrl;

        //id_Token 인증 및 uid 획득
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idTokenMap.get("idToken"));
        userId = decodedToken.getUid();

        //id 에 해당하는 db 정보 획득
        user = loginService.findByuserId(userId);
        log.info("db 에서 가져온 사용자 정보 : {}",user);

        idTokenMap.put("social_kind","google");
        serverToken = JwtUtility.makeToken(accessTokenTime, refreshTokenTime, idTokenMap, jwtsecretKey, refreshTokensecretKey);

        if (user == null) {
            log.info("새롭게 계정 등록");
            user = new User();
            user.setUserId(userId);
            user.setAuth(Auth.소셜회원);
            user.setSocial(Social.구글);
            user.setUserStatus(UserStatus.활성화);
        }
        user.setRefreshToken(serverToken.get("refresh_Token"));
        log.info("refresh_Token : {}",serverToken.get("refresh_Token"));
        em.persist(user);
        return serverToken;
    }

    // 소셜 로그인을 하는 사용자에게 서버 토큰을 반영
    /*
    * input : social_kind , socialToken( 소셜 access_Token , 소셜 refresh_Token )
    * output : 서버 토큰 ( 서버 access_Token , 서버 refresh_Token )
    * */
    @Operation(tags = "login", summary = "구글 이외의 소셜 로그인", description = "")
    @Transactional
    @PostMapping("/login/{social}")
    public HashMap<String, String> socialLogin(@PathVariable("social") String social_kind,@RequestBody SocialToken socialToken) throws Exception {
        HashMap<String, String> serverToken;
        HashMap<String, String> social_Token_map = new HashMap<>();
        Social social_enum;
        String userId;
        User user;
        String getUserUrl;

        social_Token_map.put("access_Token",socialToken.getAccess_Token());
        social_Token_map.put("refresh_Token",socialToken.getRefresh_Token());
        social_Token_map.put("social_kind",social_kind);

        //url 검사
        if(Social.카카오.getValue().equals(social_kind)){
            social_enum = Social.카카오;
            getUserUrl = socialUrlMap.get("userURLkakao");
        }
        else if(Social.네이버.getValue().equals(social_kind)){
            social_enum = Social.네이버;
            throw new LoginException("옳바르지 않은 url 입니다");
        }
        else if(Social.구글.getValue().equals(social_kind)){
            social_enum = Social.구글;
            throw new LoginException("옳바르지 않은 url 입니다");
        }
        else if(Social.페이스북.getValue().equals(social_kind)){
            social_enum = Social.페이스북;
            throw new LoginException("옳바르지 않은 url 입니다");
        }
        else{
            throw new LoginException("옳바르지 않은 url 입니다");
        }

        //유효성 검사 + id 획득
        userId = OauthUtility.getUserId(socialToken.getAccess_Token(), getUserUrl);
        user = loginService.findByuserId(userId);
        log.info("소셜 socialToken : {}",socialToken);
        serverToken = JwtUtility.makeToken(accessTokenTime, refreshTokenTime, social_Token_map, jwtsecretKey, refreshTokensecretKey);

        if (user == null) {
            log.info("새롭게 계정 등록");
            user = new User();
            user.setUserId(userId);
            user.setAuth(Auth.소셜회원);
            user.setSocial(social_enum);
            user.setUserStatus(UserStatus.활성화);
        }
        user.setRefreshToken(serverToken.get("refresh_Token"));
        log.info("refresh_Token : {}",serverToken.get("refresh_Token"));
        em.persist(user);
        return serverToken;
    }

    @Operation(tags = "login", summary = "소셜 로그아웃 (카카오만 구현)", description = "")
    @Transactional
    @PostMapping("/logout")
    public ResponseEntity logout(HttpServletRequest request) throws Exception {

        HashMap<String, String> tokenMap;
        String header = request.getHeader("Authorization");

        try {
            tokenMap = JwtUtility.getClaimData(header, jwtsecretKey, "access_Token", "refresh_Token", "id");
        }catch(ExpiredJwtException e){
            log.info("서버의 access_Token 이 만료 되었으므로 로그인 예외 발생");
            throw new LoginException("서버의 access_Token 이 만료 되었으므로 로그인 예외 발생");
        }

        String userId = tokenMap.get("id");
        User user;
        if (userId == null) {
            Boolean isRenewal = OauthUtility.isAccessTokenTimeShort(tokenMap.get("access_Token"), tokeninfo_kakao, shortTimeAccessToken);
            if (isRenewal)
                tokenMap = OauthUtility.renewalToken(jwtURL_kakao, tokenMap.get("refresh_Token"), clientID_kakao, client_secret_kakao);
            userId = OauthUtility.doLogout(tokenMap.get("access_Token"), logoutURL_kakao).toString();
        }
        user = loginService.findByuserId(userId);
        if(user == null) {
            log.warn("로그아웃 도중 db 로부터 사용자 ID 검색에 실패 하였습니다.");
        }else{
            user.setRefreshToken(null);
            em.persist(user);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @Operation(tags = "login", summary = "서버 refresh_Token 을 받고 갱신된 access_Token , refresh_Token 을 내보냄", description = "")
    @Transactional
    @PostMapping("/refresh_Token")
    public HashMap<String, String> check_Server_Refresh_Token(@RequestBody HashMap<String, String> tokenMap) throws Exception {
        User user = loginService.findByRefreshToken(tokenMap.get("refresh_Token"));
        if (user == null) {
            throw new LoginException();
        }

        boolean isExpired = JwtUtility.isExpiredRefreshToken(tokenMap.get("refresh_Token"),refreshTokensecretKey);
        if(!isExpired) {
            HashMap <String, String> social_Token = JwtUtility.getClaimDataFromRefreshToken(tokenMap.get("refresh_Token"),refreshTokensecretKey);
            if(user.getAuth().equals(Auth.비회원)) {
                social_Token.put("id",user.getUserId());
            }
            String new_access_Token = JwtUtility.makeJwtToken(accessTokenTime, social_Token, jwtsecretKey);
            tokenMap.clear();
            tokenMap.put("access_Token",new_access_Token);
            return tokenMap;
        }
        else {
            user.setRefreshToken(null);
            em.persist(user);
            throw new LoginException("서버 refresh_Token 만료. 재 로그인 해주세요");
        }
    }
}

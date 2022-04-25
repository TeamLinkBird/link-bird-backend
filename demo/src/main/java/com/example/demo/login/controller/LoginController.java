package com.example.demo.login.controller;

import com.example.demo.common.utility.JwtUtility;
import com.example.demo.common.utility.OauthUtility;
import com.example.demo.common.utility.UrlUtility;
import com.example.demo.login.entity.User;
import com.example.demo.login.service.LoginService;
import com.example.demo.login.service.OauthLoginService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
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
    public HashMap<String, String> getMainData(HttpServletRequest request){
        HashMap<String,Integer> timeMap = new HashMap<>();
        HashMap<String,String> urlMap = new HashMap<>();
        HashMap<String,String> secretMap = new HashMap<>();
        makeMap(timeMap,urlMap,secretMap);

        HashMap<String,String> socialTokenMap = LoginService.checkLocalToken(request,jwtsecretKey);
        return OauthLoginService.checkSocialToken(socialTokenMap ,timeMap ,urlMap ,secretMap);
    }

    @GetMapping("/login")
    public HashMap<String, String> login() {
        return UrlUtility.loginUrl(serverURI);
    }

    @Transactional
    @GetMapping("/unsignedLogin")
    public HashMap<String, String> unsignedLogin(@RequestBody HashMap<String,String> idMap){
        //일단 클라이언트에서 보낸 서버토큰은 확실히 없다.

        HashMap<String, String> dataMap = new HashMap<>();
        String userId = idMap.get("id");
        log.info("단말기 ID : {}",userId);
        try {
            if (userId == null) {
                throw new NullPointerException("단말기에서 전송한 ID 데이터가 없습니다.");
            }
        }catch(NullPointerException e){
        }

        dataMap.put("id",userId);
        dataMap = JwtUtility.makeToken(accessTokenTime ,refreshTokenTime ,dataMap ,jwtsecretKey);

        User user = loginService.findAByuserId(userId);

        //서버 토큰을 만들어서 내보냄
        // CASE1 ) 가져온 단말기 id의 데이터가 DB에 있으면
        // 해당id 에 해당하는 DB에 로컬 refresh_Token을 업데이트 하고 , 해당 id에 해당하는 메인정보페이지 정보를  내보냄.
        if(user!=null){
            log.info("DB 에 단말기 정보 존재 : {}",userId);
            user.setRefreshToken(dataMap.get("refresh_Token"));
            dataMap.put("link","1");
        }
        // case 2 ) 가져온 단말기 id 의 데이터가 db에 없으면
        // id를 등록하고 , 해당id 에 해당하는 DB에 로컬 refresh_Token을 저장하고
        // 메인정보페이지 정보를  내보냄.
        else{
            log.info("DB 에 단말기 정보 존재 X : {}",userId);
            user = new User();
            user.setUserId(userId);
            user.setRefreshToken(dataMap.get("refresh_Token"));
            dataMap.put("link","1");
        }
        em.persist(user);
        dataMap.put("state","main");
        return dataMap;
    }

    @Transactional
    @GetMapping("/kakaoLogin")
    public HashMap<String, String> kakaoLogin(HttpServletRequest request){
        //일단 클라이언트에서 보낸 서버토큰은 확실히 없다.

        // Oauth 의 토큰생성 절차를 거치고 , 토큰생성후
        // 로컬 토큰에 소셜 토큰을 담고,
        // 소셜 access_Token을 이용하여 사용자 회원아이디를 받아온다.
        // 받아온 사용자 회원아이디를 db에서 확인하여 , , db에 로컬 refresh_Token을 담는다.
        HashMap<String ,String> token = new HashMap<>();
        HashMap<String ,String> dataMap = null;
        HashMap<String ,Object> userinfo = new HashMap<>();
        String userId = null;
        User user = null;

        String authorize_code = request.getParameter("code");
        token = OauthUtility.getToken(authorize_code, jwtURL, clientID, request.getRequestURL().toString(), client_secret);
        userinfo = OauthUtility.getUserInfo(token.get("access_Token"),userURL);
        log.info("kakao id : {}",userinfo);
        userId = ((Long)userinfo.get("id")).toString();
        user = loginService.findAByuserId(userId);
        token = JwtUtility.makeToken(accessTokenTime ,refreshTokenTime ,token ,jwtsecretKey);
        dataMap = token;

        // CASE 1) DB 에 회원 아이디가 있다면 , 회원 아이디에 대한 메인페이지 정보와 로컬 토큰을 사용자에게 반환한다.
        // CASE 2) DB 에 회원 아이디가 없다면 , 회원 아이디를 등록하고 ,  메인페이지 정보와 로컬 토큰을 사용자에게 반환한다.
        if(user==null){
            log.info("새롭게 등록");
            user = new User();
            user.setUserId(userId);
        }
        user.setRefreshToken(token.get("refresh_Token"));
        em.persist(user);
        dataMap.put("state","main");
        dataMap.put("link","1");
        return dataMap;
    }

    @GetMapping("/kakaoLogout")
    public HashMap<String ,String> kakaoLogout(@RequestBody HashMap<String,String> dataMap ,HttpServletRequest request){
        String userId = dataMap.get("id");

/*
        로그아웃 url 에서
        서버 access_Token url의 유효성을 검사하고  ,
                만료되었다면 로그인 페이지 정보를 전달하고 ,
        서버 access_Token이 유효하다면 , 서버 access_Token  을 이용하여 다음 케이스를 따른다.*/
       /* case 1) 비회원 로그아웃
        사용자로 부터 받은 단말기 ID 를 이용하여
        DB 로부터 단말기 ID를 검색하여 refresh_Token을 null 로 만든다.
        case 2)소셜회원 로그아웃
        서버 access_Token 으로 회원 id를 소셜로부터 받고( 무효하면 갱신 후 수행 )
        소셜 토큰을 만료시킨 후
        DB 에서 받은 회원 id의 refresh_Token을 삭제한다.
                공통 ) 로컬 refresh_Token 만료 시킨 후
        로그인페이지정보 전달*/
        String header = request.getHeader("Authorization");

        // 로컬 Access_Token 이 만료 되었을 경우, 로그인 페이지 안내
        HashMap<String ,String> tokenMap = JwtUtility.getClaimData(header ,jwtsecretKey ,"access_Token","refresh_Token");

        User user = null;
        if(userId!=null){//단말기 로그인 상태
        }else{ //소셜 로그인 상태
            if(OauthUtility.isAccessTokenTimeShort(tokenMap.get("access_Token"),tokeninfoURL,oauth_accessToken_Time)) //소셜 토큰 무효하면 갱신.
                tokenMap = OauthUtility.renewalToken(jwtURL,tokenMap.get("refresh_Token"), clientID, client_secret);
            //소셜 토큰을 만료시킨다
            userId = OauthUtility.doLogout(tokenMap.get("access_Token"), kakaologoutURL).toString() ;
        }
        user = loginService.findAByuserId(userId);
        user.setRefreshToken(null);
        em.persist(user);

        return UrlUtility.loginUrl(serverURI);
    }

    public void makeMap(HashMap<String,Integer> timeMap,HashMap<String,String> urlMap,HashMap<String,String> secretMap){
        timeMap.put("accessTokenTime",accessTokenTime);
        timeMap.put("refreshTokenTime",refreshTokenTime);
        timeMap.put("oauth_accessToken_Time",oauth_accessToken_Time);
        urlMap.put("serverURI",serverURI);
        urlMap.put("tokeninfoURL",tokeninfoURL);
        urlMap.put("jwtURL",jwtURL);
        urlMap.put("userURL",userURL);
        secretMap.put("clientID",clientID);
        secretMap.put("client_secret",client_secret);
        secretMap.put("jwtsecretKey",jwtsecretKey);
    }
}

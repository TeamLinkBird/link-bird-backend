package com.example.demo.login.controller;

import com.example.demo.common.utility.JwtUtility;
import com.example.demo.common.utility.UrlUtility;
import com.example.demo.login.entity.User;
import com.example.demo.login.service.LoginService;
import com.example.demo.login.service.OauthLoginService;
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
    @PostMapping("/unsignedLogin")
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

    @PostMapping("/kakaoLogin")
    public HashMap<String, String> kakaoLogin(){
        //일단 클라이언트에서 보낸 서버토큰은 확실히 없다.

        // Oauth 의 토큰생성 절차를 거치고 , 토큰생성후
        // 로컬 토큰에 소셜 토큰을 담고 ,
        // 소셜 access_Token을 이용하여 사용자 회원아이디를 받아온다.
        // 받아온 사용자 회원아이디를 db에서 확인하여
        // CASE 1) DB 에 회원 아이디가 있다면 , 회원 아이디에 대한 메인페이지 정보와 로컬 토큰을 사용자에게 반환한다.
        // CASE 2) DB 에 회원 아이디가 없다면 , 회원 아이디를 등록하고 ,  메인페이지 정보와 로컬 토큰을 사용자에게 반환한다.
        return null;
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

package com.example.demo.login.service;

import com.example.demo.common.utility.JwtUtility;
import com.example.demo.common.utility.OauthUtility;
import com.example.demo.link.entity.Folder;
import com.example.demo.link.repository.FolderRepo;
import com.example.demo.login.entity.User;
import com.example.demo.login.repository.LoginRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class LoginService {

    @Autowired
    private LoginRepo loginRepo;

    public static HashMap<String ,String> checkLocalToken(HttpServletRequest request,String secretKey){
        HashMap<String ,String> dataMap;
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        dataMap = JwtUtility.getClaimData(authorizationHeader ,secretKey , "access_Token","refresh_Token","id");
        return dataMap;
    }

    public static HashMap<String ,String> checkLogin(HashMap<String,String> dataMap ,String access_Token){
        
        //dataMap으로 부터 userId를 받고  , 그것으로부터 main 페이지 정보를 획득
        
        dataMap.put("state","main");
        dataMap.put("link","1");
        dataMap.put("access_Token",access_Token);
        return dataMap;
    }


    public User findAByuserId(String userId){
        User user = loginRepo.findByUserId(userId);
        return user;
    }

}

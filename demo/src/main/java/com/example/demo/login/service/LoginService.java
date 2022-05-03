package com.example.demo.login.service;

import com.example.demo.login.entity.User;
import com.example.demo.login.repository.LoginRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Slf4j
@Service
public class LoginService {

    @Autowired
    private LoginRepo loginRepo;

    public static HashMap<String ,String> checkLogin(HashMap<String,String> dataMap ,String access_Token){
        //dataMap으로 부터 userId를 받고  , 그것으로부터 main 페이지 정보를 획득
        
        dataMap.put("state","main");
        dataMap.put("link","1");
        dataMap.put("access_Token",access_Token);
        return dataMap;
    }


    public User findByuserId(String userId){
        User user = loginRepo.findByUserId(userId);
        return user;
    }

    public User findByRefreshToken(String refresh_Token){
        User user = loginRepo.findByRefreshToken(refresh_Token);
        return user;
    }
}

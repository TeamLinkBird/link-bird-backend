package com.example.demo.login.service;

import com.example.demo.common.utility.JwtUtility;
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
        HashMap<String ,String> social_Token;
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        social_Token = JwtUtility.getClaimData(authorizationHeader ,secretKey , "access_Token","refresh_Token");
        return social_Token;
    }

    public User findAByuserId(String userId){
        User user = loginRepo.findByUserId(userId);
        return user;
    }

}

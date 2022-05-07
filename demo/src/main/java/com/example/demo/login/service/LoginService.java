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

    public User findByuserId(String userId){
        return loginRepo.findByUserId(userId);
    }

    public User findByRefreshToken(String refresh_Token){
        return loginRepo.findByRefreshToken(refresh_Token);
    }
}

package com.example.demo.login.controller;

import com.example.demo.common.utility.JwtUtility;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;

@Slf4j
public class LoginController {

    @PostMapping("/login")
    public String makeJwt(){
        JsonObject jo = new JsonObject();
//        jo.addProperty("jwt", JwtUtility.makeJwtToken());
        return jo.toString();
    }

}

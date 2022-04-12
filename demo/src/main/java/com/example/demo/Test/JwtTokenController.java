package com.example.demo.Test;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@Slf4j
@RestController
public class JwtTokenController {

    @PostMapping("/makeJwt")
    public String makeJwt(){
        JsonObject jo = new JsonObject();
        jo.addProperty("jwt",JwtFilter.makeJwtToken());
        return jo.toString();
    }

    @PostMapping("/setData")
    public void setData(@RequestBody HashMap<String,Object> map){
        log.info("파싱성공후 DATA : {}",(String)map.get("mybatis"));
    }
}

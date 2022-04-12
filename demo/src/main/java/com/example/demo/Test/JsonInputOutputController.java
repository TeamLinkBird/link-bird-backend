package com.example.demo.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;

@Slf4j
@RestController
public class JsonInputOutputController {

    @GetMapping("/info")
    public Object projectInfo(@RequestBody Project pro) { // 클래스로 데이터 송,수신
        log.info("가져온데이터 : {}",pro);
        Project project = new Project();
        project.projectName = "preword";
        project.author = "hello-bryan";
        project.createdDate = new Date();
        return project;
    }

    @GetMapping("/info2")
    public String customJson(@RequestBody HashMap<String,Object> map) {  // JSON 직접 제작으로 데이터 송신 , 특수한 Json데이터 수신
        log.info("random1 : {} ",map.get("random1"));
        log.info("random2 : {} ",map.get("random2"));

        JsonObject jo = new JsonObject();

        jo.addProperty("projectName", "preword");
        jo.addProperty("author", "hello-bryan");
        jo.addProperty("createdDate", new Date().toString());

        JsonArray ja = new JsonArray();
        for(int i=0; i<5; i++) {
            JsonObject jObj = new JsonObject();
            jObj.addProperty("prop"+i, i);
            ja.add(jObj);
        }

        jo.add("follower", ja);  // jo(json) 내부에 ja(json배열) 내부에 jobj(json)들 있는 형태

        return jo.toString();
    }
}

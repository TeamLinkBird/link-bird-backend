package com.example.demo.link.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.Assertions.*;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FolderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String url = "/folders";

    @DisplayName("폴더 정보 조회")
    @Test
    public void findFolderByFolderCode() throws Exception{
        //given
        long folderCode = 1;

        //when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(url + "/" + folderCode))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        //then
        JSONObject jsonResult = new JSONObject(result.getResponse().getContentAsString());
        assertThat(jsonResult.getLong("code")).isEqualTo(1);
    }
}

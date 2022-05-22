package com.example.demo.login.dto;

import lombok.Data;

@Data
public class SocialToken {
    private String access_Token;
    private String refresh_Token;
}

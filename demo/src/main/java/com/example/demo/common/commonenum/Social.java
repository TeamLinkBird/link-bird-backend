package com.example.demo.common.commonenum;

public enum Social implements CodeValue {
    없음("1", "no"),
    카카오("2", "kakao"),
    네이버("3", "naver"),
    구글("4", "google"),
    페이스북("5", "facebook");


    private String code;
    private String value;

    Social(String code, String value) {
        this.code = code;
        this.value = value;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getValue() {
        return value;
    }
}

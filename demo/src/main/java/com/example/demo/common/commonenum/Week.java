package com.example.demo.common.commonenum;

public enum Week implements CodeValue {

    월요일("1", "월요일"),
    화요일("2", "화요일"),
    수요일("3", "수요일"),
    목요일("4", "목요일"),
    금요일("5", "금요일"),
    토요일("6", "토요일"),
    일요일("7", "일요일");


    private String code;
    private String value;

    Week(String code, String value) {
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

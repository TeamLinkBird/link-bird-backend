package com.example.demo.common.commonenum;

public enum BackGround implements CodeValue {

    빨간색("1", "빨간색"),
    주황색("2", "주황색"),
    노란색("4", "노란색"),
    초란색("5", "초란색"),
    파란색("6", "파란색"),
    검란색("7", "검란색");

    private String code;
    private String value;

    BackGround(String code, String value) {
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

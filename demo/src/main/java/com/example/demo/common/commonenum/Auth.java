package com.example.demo.common.commonenum;

public enum Auth implements CodeValue {
    관리자("1", "관리자"),
    비회원("2", "비회원"),
    소셜회원("3", "소셜회원");

    private String code;
    private String value;

    Auth(String code, String value) {
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

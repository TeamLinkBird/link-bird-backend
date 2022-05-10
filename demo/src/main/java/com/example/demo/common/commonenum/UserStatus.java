package com.example.demo.common.commonenum;

public enum UserStatus implements CodeValue {
    탈퇴("1", "탈퇴"),
    활성화("2", "활성화"),
    휴면("3", "휴면");

    private String code;
    private String value;

    UserStatus(String code, String value) {
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

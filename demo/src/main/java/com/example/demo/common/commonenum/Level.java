package com.example.demo.common.commonenum;

public enum Level implements CodeValue {

    easy("1", "easy"),
    normal("2", "normal"),
    hard("3", "hard");

    private String code;
    private String value;

    Level(String code, String value) {
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

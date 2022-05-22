package com.example.demo.login.exception;

public class AccessUrlException extends RuntimeException{

    private String msg;

    public AccessUrlException() {
        super();
    }
    public AccessUrlException(String _msg) {
        super(_msg);
        this.msg=_msg;
    }

    public String getMessage() {
        return msg;
    }

}

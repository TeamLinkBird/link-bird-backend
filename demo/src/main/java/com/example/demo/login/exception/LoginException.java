package com.example.demo.login.exception;

public class LoginException extends RuntimeException{

    private String msg;

    public LoginException() {
        super();
    }
    public LoginException(String _msg) {
        super(_msg);
        this.msg=_msg;
    }

    public String getMessage() {
        return msg;
    }

}

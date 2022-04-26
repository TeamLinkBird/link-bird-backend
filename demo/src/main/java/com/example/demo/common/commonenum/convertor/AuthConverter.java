package com.example.demo.common.commonenum.convertor;

import com.example.demo.common.commonenum.Auth;

import javax.persistence.Converter;

@Converter(autoApply = true)
public class AuthConverter extends CodeValueConverter<Auth> {
    AuthConverter() {
        super(Auth.class);
    }
}

package com.example.demo.common.commonenum.convertor;

import com.example.demo.common.commonenum.Auth;
import com.example.demo.common.commonenum.Social;

import javax.persistence.Converter;

@Converter(autoApply = true)
public class SocialConverter extends CodeValueConverter<Social>{
    SocialConverter() {
        super(Social.class);
    }
}



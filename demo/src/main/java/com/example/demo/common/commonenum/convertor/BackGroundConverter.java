package com.example.demo.common.commonenum.convertor;

import com.example.demo.common.commonenum.Auth;
import com.example.demo.common.commonenum.BackGround;

import javax.persistence.Converter;

@Converter(autoApply = true)
public class BackGroundConverter extends CodeValueConverter<BackGround> {
    BackGroundConverter() {
        super(BackGround.class);
    }
}

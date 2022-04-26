package com.example.demo.common.commonenum.convertor;

import com.example.demo.common.commonenum.Level;

import javax.persistence.Converter;

@Converter(autoApply = true)
public class LevelConverter extends CodeValueConverter<Level>{
    LevelConverter() {
        super(Level.class);
    }
}

package com.example.demo.common.commonenum.convertor;

import com.example.demo.common.commonenum.BackGround;
import com.example.demo.common.commonenum.Week;

import javax.persistence.Converter;

@Converter(autoApply = true)
public class WeekConverter extends CodeValueConverter<Week> {
    WeekConverter() {
        super(Week.class);
    }
}

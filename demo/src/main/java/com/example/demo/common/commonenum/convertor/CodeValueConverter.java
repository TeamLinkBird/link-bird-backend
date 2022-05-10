package com.example.demo.common.commonenum.convertor;

import com.example.demo.common.commonenum.CodeValue;

import javax.persistence.AttributeConverter;
import java.util.EnumSet;
import java.util.NoSuchElementException;

public class CodeValueConverter<E extends Enum<E> & CodeValue> implements AttributeConverter<E, String> {

    private Class<E> clz;

    CodeValueConverter(Class<E> enumClass){
        this.clz = enumClass;
    }

    @Override
    public String convertToDatabaseColumn(E attribute) {
        return attribute.getCode();
    }

    @Override
    public E convertToEntityAttribute(String dbData) {
        return EnumSet.allOf(clz).stream()
                .filter(e->e.getCode().equals(dbData))
                .findAny()
                .orElseThrow(()-> new NoSuchElementException());
    }

}

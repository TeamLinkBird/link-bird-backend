package com.example.demo.common.commonenum.convertor;

import com.example.demo.common.commonenum.Auth;
import com.example.demo.common.commonenum.Social;
import com.example.demo.common.commonenum.UserStatus;

import javax.persistence.Converter;

@Converter(autoApply = true)
public class UserStatusConverter extends CodeValueConverter<UserStatus> {
    UserStatusConverter() {
        super(UserStatus.class);
    }
}

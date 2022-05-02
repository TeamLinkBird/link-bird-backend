package com.example.demo.login.entity;

import com.example.demo.common.commonenum.Auth;
import com.example.demo.common.commonenum.Social;
import com.example.demo.common.commonenum.UserStatus;
import com.example.demo.common.commonenum.convertor.AuthConverter;
import com.example.demo.common.commonenum.convertor.SocialConverter;
import com.example.demo.common.commonenum.convertor.UserStatusConverter;
import com.example.demo.common.entity.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "user")
public class User extends BaseTimeEntity {

    @Id
    private String userId;


    @Convert(converter = AuthConverter.class)
    private Auth auth;

    @Convert(converter = SocialConverter.class)
    private Social social;

    @Convert(converter = UserStatusConverter.class)
    private UserStatus userStatus;

    private String userMail;

    private String userNm;

    @Column(unique = true)
    private String refreshToken;

    private String socialAccessToken;

    private String socialRefreshToken;
}

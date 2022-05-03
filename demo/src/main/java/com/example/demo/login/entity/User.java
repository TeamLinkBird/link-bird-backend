package com.example.demo.login.entity;

import com.example.demo.common.commonenum.Auth;
import com.example.demo.common.commonenum.Social;
import com.example.demo.common.commonenum.UserStatus;
import com.example.demo.common.commonenum.convertor.AuthConverter;
import com.example.demo.common.commonenum.convertor.SocialConverter;
import com.example.demo.common.commonenum.convertor.UserStatusConverter;
import com.example.demo.common.entity.BaseTimeEntity;
import com.example.demo.link.entity.Folder;
import com.example.demo.link.entity.Link;
import com.example.demo.link.entity.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "user")
    private List<Folder> folders;

    @OneToMany(mappedBy = "user")
    private List<Link> link;

    @OneToMany(mappedBy = "user")
    private List<Tag> tags;
}

package com.example.demo.login.entity;

import com.example.demo.link.entity.Link;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.annotations.Many;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "user")
public class User {

    @Id
    private String userId;

/*    @ManyToOne
    @JoinColumn(name="AUTH_CD")
    private Auth auth;

    @OneToOne
    @JoinColumn(name="SOCIAL_CD")
    private Social social;

    @ManyToOne
    @JoinColumn(name="USERSTATUS_CD")
    private Userstatus userStatus;*/

    private String userMail;

    private String userNm;

    @CreatedDate
    private LocalDateTime userRegDt;

    @LastModifiedDate
    private LocalDateTime loginDt;

    private String refreshToken;

}

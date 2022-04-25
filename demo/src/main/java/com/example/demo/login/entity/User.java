package com.example.demo.login.entity;

import com.example.demo.common.entity.BaseTimeEntity;
import com.example.demo.link.entity.Link;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.annotations.Many;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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

    private String refreshToken;

}

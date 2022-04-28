package com.example.demo.link.entity;

import com.example.demo.login.entity.User;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity(name = "tag")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long tagCode;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "link_code")
    private Link link;

    private int tagOrder;
    private String tagName;

    @CreatedDate
    private LocalDateTime tagRegisterDate;

    @LastModifiedDate
    private LocalDateTime tagUpdateDate;

    @Builder
    private Tag(User user, Link link, int tagOrder, String tagName){
        this.user = user;
        this.link = link;
        this.tagOrder = tagOrder;
        this.tagName = tagName;
    }
}

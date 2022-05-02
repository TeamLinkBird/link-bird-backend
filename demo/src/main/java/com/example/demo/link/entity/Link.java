package com.example.demo.link.entity;

import com.example.demo.login.entity.User;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity(name = "link")
public class Link {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long linkCode;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "folder_id")
    private Folder folder;

    private int linkOrder;

    //todo add colum levelCode

    private String url;
    private String title;
    private String memo;
    private boolean isRead;
    private LocalDateTime readDate;

    @CreatedDate
    private LocalDateTime linkRegisterDate;

    @LastModifiedDate
    private LocalDateTime linkUpdateDate;

    @Builder
    private Link(User user, Folder folder, int linkOrder, String url, String title, String memo
    , boolean isRead){
        this.user = user;
        this.folder = folder;
        this.linkOrder = linkOrder;
        this.url = url;
        this.title = title;
        this.memo = memo;
        this.isRead = isRead;
    }

}

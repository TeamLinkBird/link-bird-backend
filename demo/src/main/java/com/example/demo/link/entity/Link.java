package com.example.demo.link.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "link")
public class Link {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long linkCode;

    //todo add column userId

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

}

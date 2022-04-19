package com.example.demo.link.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Folder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "folder_code")
    private long folderCode;

    //todo add column user_id

    private int folderOrder;
    private String folderName;

    @CreatedDate
    private LocalDateTime folderRegisterDate;

    @LastModifiedDate
    private LocalDateTime folderUpdateDate;

}

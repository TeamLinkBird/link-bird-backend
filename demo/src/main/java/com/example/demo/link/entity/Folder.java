package com.example.demo.link.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "folder")
public class Folder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "folder_code")
    private long folderCode;

    //todo add column user_id

    private int folderOrder;
    private String folderName;

    @CreatedDate
    private LocalDateTime folderRegisterDate;

    @LastModifiedDate
    private LocalDateTime folderUpdateDate;

    @OneToMany(mappedBy = "folder")
    private List<Link> links = new ArrayList<>();

}

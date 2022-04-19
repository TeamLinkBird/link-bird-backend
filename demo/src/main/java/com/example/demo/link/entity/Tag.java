package com.example.demo.link.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "tag")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long tagCode;

    //todo add column userId

    @ManyToOne
    @JoinColumn(name = "link_code")
    private Link link;

    private int tagOrder;
    private String tagName;

    @CreatedDate
    private LocalDateTime tagRegisterDate;

    @LastModifiedDate
    private LocalDateTime tagUpdateDate;
}

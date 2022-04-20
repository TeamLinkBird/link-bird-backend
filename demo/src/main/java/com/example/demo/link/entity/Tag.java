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

package com.example.demo.link.entity;

import com.example.demo.login.entity.User;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity(name = "folder")
public class Folder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "folder_code")
    private long folderCode;

    //todo add column user_id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private int folderOrder;
    private String folderName;

    @CreatedDate
    private LocalDateTime folderRegisterDate;

    @LastModifiedDate
    private LocalDateTime folderUpdateDate;

    @OneToMany(mappedBy = "folder")
    private List<Link> links = new ArrayList<>();

    @Builder
    private Folder(User user, long folderCode, int folderOrder, String folderName){
        this.user = user;
        this.folderCode = folderCode;
        this.folderOrder = folderOrder;
        this.folderName = folderName;
    }

}

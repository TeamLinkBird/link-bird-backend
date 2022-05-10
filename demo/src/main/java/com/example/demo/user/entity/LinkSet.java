package com.example.demo.user.entity;

import com.example.demo.common.commonenum.Week;
import com.example.demo.common.commonenum.convertor.WeekConverter;
import com.example.demo.link.entity.Link;
import com.example.demo.login.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "linkset")
public class LinkSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cd;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "linkCode")
    private Link link;

}

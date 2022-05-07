package com.example.demo.user.entity;

import com.example.demo.common.commonenum.BackGround;
import com.example.demo.common.commonenum.Week;
import com.example.demo.common.commonenum.convertor.BackGroundConverter;
import com.example.demo.common.commonenum.convertor.WeekConverter;
import com.example.demo.login.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "weekset")
public class WeekSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cd;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Convert(converter = WeekConverter.class)
    private Week week;

}

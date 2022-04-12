package com.example.demo.Test;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class Project {
    public String projectName;
    public String author;
    public Date createdDate;
}

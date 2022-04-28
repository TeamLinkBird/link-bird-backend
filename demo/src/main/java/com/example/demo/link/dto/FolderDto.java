package com.example.demo.link.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class FolderDto {
    private String userId;
    private Long code;
    private int order;
    private String name;
    private List<LinkDto> list;


    @Builder
    private FolderDto(String userId, Long code, String name,List<LinkDto> list) {
        this.userId = userId;
        this.code = code;
        this.list = list;
        this.name = name;
    }
}

package com.example.demo.link.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TagDto {

    private long linkCode;

    private int tagOrder;
    private String tagName;

    @Builder
    private TagDto(long linkCode, int tagOrder, String tagName){
        this.linkCode = linkCode;
        this.tagOrder = tagOrder;
        this.tagName = tagName;
    }
}

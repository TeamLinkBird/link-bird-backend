package com.example.demo.link.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LinkDto {

    private long folderCode;

    private int linkOrder;

    private String url;
    private String title;
    private String memo;
    private boolean isRead;

    @Builder
    private LinkDto(long folderCode, int linkOrder, String url, String title,
                    String memo, boolean isRead) {
        this.folderCode = folderCode;
        this.linkOrder = linkOrder;
        this.url = url;
        this.title = title;
        this.memo = memo;
        this.isRead = isRead;
    }
}

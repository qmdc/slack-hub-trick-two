package com.slack.slackjarservice.bookmarkmanager.model.dto;

import lombok.Data;

@Data
public class BookmarkDTO {

    private Long id;

    private String url;

    private String title;

    private String faviconUrl;

    private String description;

    private String tags;

    private Long categoryId;

    private String categoryName;

    private Long createTime;

    private Long updateTime;
}
package com.slack.slackjarservice.bookmarkmanager.model.dto;

import lombok.Data;

@Data
public class BookmarkCategoryDTO {

    private Long id;

    private String name;

    private String icon;

    private Integer sortOrder;

    private Long createTime;
}
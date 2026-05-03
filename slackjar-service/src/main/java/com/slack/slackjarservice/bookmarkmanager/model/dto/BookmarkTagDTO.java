package com.slack.slackjarservice.bookmarkmanager.model.dto;

import lombok.Data;

@Data
public class BookmarkTagDTO {

    private Long id;

    private String name;

    private String color;

    private Long createTime;
}
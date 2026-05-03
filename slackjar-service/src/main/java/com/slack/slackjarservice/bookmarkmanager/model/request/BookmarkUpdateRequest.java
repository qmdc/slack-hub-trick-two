package com.slack.slackjarservice.bookmarkmanager.model.request;

import lombok.Data;

@Data
public class BookmarkUpdateRequest {

    private String title;

    private String description;

    private String tags;

    private Long categoryId;
}
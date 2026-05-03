package com.slack.slackjarservice.bookmarkmanager.model.request;

import lombok.Data;

import java.util.List;

@Data
public class BookmarkImportRequest {

    private List<BookmarkItem> bookmarks;

    @Data
    public static class BookmarkItem {
        private String url;
        private String title;
        private String description;
        private String tags;
        private String category;
    }
}
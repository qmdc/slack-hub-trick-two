package com.slack.slackjarservice.mediarecommend.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class MediaItemDTO {

    private Long id;

    private String title;

    private Integer type;

    private Integer status;

    private Integer rating;

    private String review;

    private List<String> tags;

    private String coverUrl;

    private String author;

    private String year;

    private Long createTime;

    private Long updateTime;
}
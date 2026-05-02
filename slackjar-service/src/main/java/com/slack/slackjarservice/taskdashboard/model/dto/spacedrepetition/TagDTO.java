package com.slack.slackjarservice.taskdashboard.model.dto.spacedrepetition;

import lombok.Data;

import java.io.Serializable;

/**
 * 标签DTO
 */
@Data
public class TagDTO implements Serializable {

    private Long id;

    private Long userId;

    private String name;

    private String color;

    private Integer cardCount;

    private Integer sortOrder;

    private Long createTime;

    private Long updateTime;
}

package com.slack.slackjarservice.emotionchat.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionListResponse {

    private Long id;

    private String sessionName;

    private String lastMessage;

    private String lastEmotion;

    private Integer unreadCount;

    private Long updateTime;
}
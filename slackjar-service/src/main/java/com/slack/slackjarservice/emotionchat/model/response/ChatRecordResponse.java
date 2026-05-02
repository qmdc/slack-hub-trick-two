package com.slack.slackjarservice.emotionchat.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRecordResponse {

    private Long id;

    private String message;

    private Integer isUser;

    private String emotion;

    private Long createTime;
}
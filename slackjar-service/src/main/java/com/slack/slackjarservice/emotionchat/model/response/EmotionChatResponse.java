package com.slack.slackjarservice.emotionchat.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmotionChatResponse {

    private Long sessionId;

    private String response;

    private String emotion;

    private Double emotionScore;

    private String strategy;
}
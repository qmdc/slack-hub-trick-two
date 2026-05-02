package com.slack.slackjarservice.emotionchat.service;

import com.slack.slackjarservice.emotionchat.model.request.EmotionChatRequest;
import com.slack.slackjarservice.emotionchat.model.request.SessionCreateRequest;
import com.slack.slackjarservice.emotionchat.model.response.ChatRecordResponse;
import com.slack.slackjarservice.emotionchat.model.response.EmotionChatResponse;
import com.slack.slackjarservice.emotionchat.model.response.SessionListResponse;

import java.util.List;

public interface EmotionChatService {

    List<SessionListResponse> getSessionList(Long userId);

    Long createSession(Long userId, SessionCreateRequest request);

    void deleteSession(Long sessionId);

    List<ChatRecordResponse> getChatRecords(Long sessionId);

    EmotionChatResponse chat(Long userId, EmotionChatRequest request);

    String analyzeEmotion(String message);

    String getComfortMessage(String emotionType, List<String> recentEmotions);
}
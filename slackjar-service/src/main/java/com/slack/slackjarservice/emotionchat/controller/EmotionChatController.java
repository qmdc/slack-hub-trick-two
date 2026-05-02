package com.slack.slackjarservice.emotionchat.controller;

import com.slack.slackjarservice.common.base.BaseController;
import com.slack.slackjarservice.common.response.ApiResponse;
import com.slack.slackjarservice.emotionchat.model.request.EmotionChatRequest;
import com.slack.slackjarservice.emotionchat.model.request.SessionCreateRequest;
import com.slack.slackjarservice.emotionchat.model.response.ChatRecordResponse;
import com.slack.slackjarservice.emotionchat.model.response.EmotionChatResponse;
import com.slack.slackjarservice.emotionchat.model.response.SessionListResponse;
import com.slack.slackjarservice.emotionchat.service.EmotionChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/emotion-chat")
@RequiredArgsConstructor
@Validated
public class EmotionChatController extends BaseController {

    private final EmotionChatService emotionChatService;

    @GetMapping("/sessions")
    public ApiResponse<List<SessionListResponse>> getSessionList() {
        Long userId = getLoginUserId();
        List<SessionListResponse> sessions = emotionChatService.getSessionList(userId);
        return success(sessions);
    }

    @PostMapping("/sessions")
    public ApiResponse<Long> createSession(@RequestBody(required = false) SessionCreateRequest request) {
        Long userId = getLoginUserId();
        if (request == null) {
            request = new SessionCreateRequest();
        }
        Long sessionId = emotionChatService.createSession(userId, request);
        return success(sessionId);
    }

    @DeleteMapping("/sessions/{sessionId}")
    public ApiResponse<Void> deleteSession(@PathVariable @NotNull Long sessionId) {
        emotionChatService.deleteSession(sessionId);
        return success();
    }

    @GetMapping("/sessions/{sessionId}/records")
    public ApiResponse<List<ChatRecordResponse>> getChatRecords(@PathVariable @NotNull Long sessionId) {
        List<ChatRecordResponse> records = emotionChatService.getChatRecords(sessionId);
        return success(records);
    }

    @PostMapping("/chat")
    public ApiResponse<EmotionChatResponse> chat(@Valid @RequestBody EmotionChatRequest request) {
        Long userId = getLoginUserId();
        EmotionChatResponse response = emotionChatService.chat(userId, request);
        return success(response);
    }
}
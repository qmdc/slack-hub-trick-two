    package com.slack.slackjarservice.emotionchat.service.impl;

import com.slack.slackjarservice.emotionchat.dao.ComfortMessageDao;
import com.slack.slackjarservice.emotionchat.dao.EmotionChatRecordDao;
import com.slack.slackjarservice.emotionchat.dao.EmotionChatSessionDao;
import com.slack.slackjarservice.emotionchat.entity.ComfortMessage;
import com.slack.slackjarservice.emotionchat.entity.EmotionChatRecord;
import com.slack.slackjarservice.emotionchat.entity.EmotionChatSession;
import com.slack.slackjarservice.emotionchat.model.request.EmotionChatRequest;
import com.slack.slackjarservice.emotionchat.model.request.SessionCreateRequest;
import com.slack.slackjarservice.emotionchat.model.response.ChatRecordResponse;
import com.slack.slackjarservice.emotionchat.model.response.EmotionChatResponse;
import com.slack.slackjarservice.emotionchat.model.response.SessionListResponse;
import com.slack.slackjarservice.emotionchat.service.EmotionChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmotionChatServiceImpl implements EmotionChatService {

    private final EmotionChatSessionDao sessionDao;
    private final EmotionChatRecordDao recordDao;
    private final ComfortMessageDao comfortMessageDao;

    private static final Pattern SAD_PATTERN = Pattern.compile("难过|伤心|失望|沮丧|失落|想哭|难受|心碎|痛苦|郁闷");
    private static final Pattern ANGRY_PATTERN = Pattern.compile("生气|愤怒|讨厌|烦|烦透了|气死|火大|暴怒");
    private static final Pattern HAPPY_PATTERN = Pattern.compile("开心|高兴|快乐|幸福|兴奋|太棒|太好了|喜悦");
    private static final Pattern ANXIOUS_PATTERN = Pattern.compile("焦虑|担心|害怕|紧张|不安|忧虑|发愁|忐忑");
    private static final Pattern TIRED_PATTERN = Pattern.compile("累|疲惫|疲倦|困|想睡|乏力|心累");

    @Override
    public List<SessionListResponse> getSessionList(Long userId) {
        List<EmotionChatSession> sessions = sessionDao.selectByUserId(userId);
        return sessions.stream()
                .map(this::convertToSessionResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Long createSession(Long userId, SessionCreateRequest request) {
        EmotionChatSession session = new EmotionChatSession();
        session.setUserId(userId);
        session.setSessionName(request.getSessionName() != null ? request.getSessionName() : "新对话");
        session.setUnreadCount(0);
        session.setCreateTime(System.currentTimeMillis());
        session.setUpdateTime(System.currentTimeMillis());
        sessionDao.insert(session);
        return session.getId();
    }

    @Override
    @Transactional
    public void deleteSession(Long sessionId) {
        EmotionChatSession session = sessionDao.selectById(sessionId);
        if (session != null) {
            session.setDeleted(1);
            sessionDao.updateById(session);

            List<EmotionChatRecord> records = recordDao.selectBySessionId(sessionId);
            for (EmotionChatRecord record : records) {
                record.setDeleted(1);
                recordDao.updateById(record);
            }
        }
    }

    @Override
    public List<ChatRecordResponse> getChatRecords(Long sessionId) {
        List<EmotionChatRecord> records = recordDao.selectBySessionId(sessionId);
        return records.stream()
                .map(this::convertToRecordResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EmotionChatResponse chat(Long userId, EmotionChatRequest request) {
        Long sessionId = request.getSessionId();

        if (sessionId == null) {
            sessionId = createSession(userId, new SessionCreateRequest());
        }

        String emotion = analyzeEmotion(request.getMessage());
        List<String> recentEmotions = getRecentEmotions(userId);

        String comfortMessage = getComfortMessage(emotion, recentEmotions);

        EmotionChatRecord userRecord = new EmotionChatRecord();
        userRecord.setSessionId(sessionId);
        userRecord.setUserId(userId);
        userRecord.setMessage(request.getMessage());
        userRecord.setIsUser(1);
        userRecord.setEmotion(emotion);
        userRecord.setEmotionScore(0.8);
        userRecord.setCreateTime(System.currentTimeMillis());
        recordDao.insert(userRecord);

        EmotionChatRecord systemRecord = new EmotionChatRecord();
        systemRecord.setSessionId(sessionId);
        systemRecord.setUserId(userId);
        systemRecord.setMessage(comfortMessage);
        systemRecord.setIsUser(0);
        systemRecord.setEmotion(emotion);
        systemRecord.setResponseStrategy("auto");
        systemRecord.setCreateTime(System.currentTimeMillis());
        recordDao.insert(systemRecord);

        updateSession(sessionId, comfortMessage, emotion);

        return EmotionChatResponse.builder()
                .sessionId(sessionId)
                .response(comfortMessage)
                .emotion(emotion)
                .emotionScore(0.8)
                .strategy("auto")
                .build();
    }

    @Override
    public String analyzeEmotion(String message) {
        if (SAD_PATTERN.matcher(message).find()) {
            return "sad";
        }
        if (ANGRY_PATTERN.matcher(message).find()) {
            return "angry";
        }
        if (HAPPY_PATTERN.matcher(message).find()) {
            return "happy";
        }
        if (ANXIOUS_PATTERN.matcher(message).find()) {
            return "anxious";
        }
        if (TIRED_PATTERN.matcher(message).find()) {
            return "tired";
        }
        return "neutral";
    }

    @Override
    public String getComfortMessage(String emotionType, List<String> recentEmotions) {
        List<ComfortMessage> messages = comfortMessageDao.selectByEmotionType(emotionType);
        if (messages.isEmpty()) {
            messages = comfortMessageDao.selectByEmotionType("neutral");
        }

        if (messages.isEmpty()) {
            return "有什么我可以帮你的吗？";
        }

        Map<String, Integer> emotionCount = new HashMap<>();
        for (String emotion : recentEmotions) {
            emotionCount.merge(emotion, 1, Integer::sum);
        }

        final String messageType = emotionCount.getOrDefault(emotionType, 0) >= 3 ? "encourage" : "default";

        List<ComfortMessage> filtered = messages.stream()
                .filter(m -> m.getMessageType().equals(messageType) || m.getMessageType().equals("default"))
                .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            filtered = messages;
        }

        Collections.shuffle(filtered);
        return filtered.get(0).getContent();
    }

    private List<String> getRecentEmotions(Long userId) {
        List<EmotionChatRecord> records = recordDao.selectRecentByUserId(userId, 10);
        return records.stream()
                .filter(r -> r.getIsUser() == 1 && r.getEmotion() != null)
                .map(EmotionChatRecord::getEmotion)
                .collect(Collectors.toList());
    }

    private void updateSession(Long sessionId, String lastMessage, String emotion) {
        EmotionChatSession session = sessionDao.selectById(sessionId);
        if (session != null) {
            session.setLastMessage(lastMessage);
            session.setLastEmotion(emotion);
            session.setUpdateTime(System.currentTimeMillis());
            sessionDao.updateById(session);
        }
    }

    private SessionListResponse convertToSessionResponse(EmotionChatSession session) {
        return SessionListResponse.builder()
                .id(session.getId())
                .sessionName(session.getSessionName())
                .lastMessage(session.getLastMessage())
                .lastEmotion(session.getLastEmotion())
                .unreadCount(session.getUnreadCount())
                .updateTime(session.getUpdateTime())
                .build();
    }

    private ChatRecordResponse convertToRecordResponse(EmotionChatRecord record) {
        return ChatRecordResponse.builder()
                .id(record.getId())
                .message(record.getMessage())
                .isUser(record.getIsUser())
                .emotion(record.getEmotion())
                .createTime(record.getCreateTime())
                .build();
    }
}
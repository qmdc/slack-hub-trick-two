package com.slack.slackjarservice.mediarecommend.service;

import com.slack.slackjarservice.mediarecommend.model.dto.MediaItemDTO;
import com.slack.slackjarservice.mediarecommend.model.request.MediaItemRequest;
import com.slack.slackjarservice.mediarecommend.model.request.ShareRequest;

import java.util.List;
import java.util.Map;

public interface MediaRecommendService {

    MediaItemDTO addItem(MediaItemRequest request, Long userId);

    MediaItemDTO updateItem(MediaItemRequest request, Long userId);

    void deleteItem(Long id, Long userId);

    MediaItemDTO getItemById(Long id, Long userId);

    List<MediaItemDTO> listItems(Long userId, Integer type, Integer status);

    List<MediaItemDTO> getRecommendations(Long userId, Integer limit);

    List<String> getAllTags(Long userId);

    Map<String, Object> createShareLink(Long userId, ShareRequest request);

    Map<String, Object> getSharedItems(String shareCode);

    void deleteShareLink(Long userId, String shareCode);

    List<Map<String, Object>> listShareLinks(Long userId);
}
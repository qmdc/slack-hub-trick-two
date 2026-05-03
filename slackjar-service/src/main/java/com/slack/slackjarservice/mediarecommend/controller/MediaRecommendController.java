package com.slack.slackjarservice.mediarecommend.controller;

import com.slack.slackjarservice.common.base.BaseController;
import com.slack.slackjarservice.common.response.ApiResponse;
import com.slack.slackjarservice.mediarecommend.model.dto.MediaItemDTO;
import com.slack.slackjarservice.mediarecommend.model.request.MediaItemRequest;
import com.slack.slackjarservice.mediarecommend.model.request.ShareRequest;
import com.slack.slackjarservice.mediarecommend.service.MediaRecommendService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/media")
public class MediaRecommendController extends BaseController {

    @Resource
    private MediaRecommendService mediaRecommendService;

    @PostMapping("/item")
    public ApiResponse<MediaItemDTO> addItem(@Valid @RequestBody MediaItemRequest request) {
        Long userId = getLoginUserId();
        MediaItemDTO item = mediaRecommendService.addItem(request, userId);
        return success(item);
    }

    @PutMapping("/item")
    public ApiResponse<MediaItemDTO> updateItem(@Valid @RequestBody MediaItemRequest request) {
        Long userId = getLoginUserId();
        MediaItemDTO item = mediaRecommendService.updateItem(request, userId);
        return success(item);
    }

    @DeleteMapping("/item/{id}")
    public ApiResponse<Void> deleteItem(@PathVariable Long id) {
        Long userId = getLoginUserId();
        mediaRecommendService.deleteItem(id, userId);
        return success();
    }

    @GetMapping("/item/{id}")
    public ApiResponse<MediaItemDTO> getItemById(@PathVariable Long id) {
        Long userId = getLoginUserId();
        MediaItemDTO item = mediaRecommendService.getItemById(id, userId);
        return success(item);
    }

    @GetMapping("/items")
    public ApiResponse<List<MediaItemDTO>> listItems(
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {
        Long userId = getLoginUserId();
        List<MediaItemDTO> items = mediaRecommendService.listItems(userId, type, status);
        return success(items);
    }

    @GetMapping("/recommendations")
    public ApiResponse<List<MediaItemDTO>> getRecommendations(
            @RequestParam(required = false) Integer limit) {
        Long userId = getLoginUserId();
        List<MediaItemDTO> recommendations = mediaRecommendService.getRecommendations(userId, limit);
        return success(recommendations);
    }

    @GetMapping("/tags")
    public ApiResponse<List<String>> getAllTags() {
        Long userId = getLoginUserId();
        List<String> tags = mediaRecommendService.getAllTags(userId);
        return success(tags);
    }

    @PostMapping("/share")
    public ApiResponse<Map<String, Object>> createShareLink(@RequestBody ShareRequest request) {
        Long userId = getLoginUserId();
        Map<String, Object> result = mediaRecommendService.createShareLink(userId, request);
        return success(result);
    }

    @GetMapping("/share/{shareCode}")
    public ApiResponse<Map<String, Object>> getSharedItems(@PathVariable String shareCode) {
        Map<String, Object> result = mediaRecommendService.getSharedItems(shareCode);
        return success(result);
    }

    @DeleteMapping("/share/{shareCode}")
    public ApiResponse<Void> deleteShareLink(@PathVariable String shareCode) {
        Long userId = getLoginUserId();
        mediaRecommendService.deleteShareLink(userId, shareCode);
        return success();
    }

    @GetMapping("/shares")
    public ApiResponse<List<Map<String, Object>>> listShareLinks() {
        Long userId = getLoginUserId();
        List<Map<String, Object>> links = mediaRecommendService.listShareLinks(userId);
        return success(links);
    }
}
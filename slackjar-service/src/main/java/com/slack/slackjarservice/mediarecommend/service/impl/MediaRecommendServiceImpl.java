package com.slack.slackjarservice.mediarecommend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.slack.slackjarservice.common.exception.BusinessException;
import com.slack.slackjarservice.common.enumtype.foundation.ResponseEnum;
import com.slack.slackjarservice.common.util.RandomUtil;
import com.slack.slackjarservice.mediarecommend.dao.MediaItemDao;
import com.slack.slackjarservice.mediarecommend.dao.ShareLinkDao;
import com.slack.slackjarservice.mediarecommend.entity.MediaItem;
import com.slack.slackjarservice.mediarecommend.entity.ShareLink;
import com.slack.slackjarservice.mediarecommend.model.dto.MediaItemDTO;
import com.slack.slackjarservice.mediarecommend.model.request.MediaItemRequest;
import com.slack.slackjarservice.mediarecommend.model.request.ShareRequest;
import com.slack.slackjarservice.mediarecommend.service.MediaRecommendService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MediaRecommendServiceImpl implements MediaRecommendService {

    @Resource
    private MediaItemDao mediaItemDao;

    @Resource
    private ShareLinkDao shareLinkDao;

    private static final int RECOMMENDATION_LIMIT = 10;

    @Override
    @Transactional
    public MediaItemDTO addItem(MediaItemRequest request, Long userId) {
        MediaItem item = new MediaItem();
        item.setUserId(userId);
        item.setTitle(request.getTitle());
        item.setType(request.getType());
        item.setStatus(request.getStatus());
        item.setRating(request.getRating());
        item.setReview(request.getReview());
        item.setTags(convertTagsToString(request.getTags()));
        item.setCoverUrl(request.getCoverUrl());
        item.setAuthor(request.getAuthor());
        item.setYear(request.getYear());
        mediaItemDao.insert(item);
        return convertToDTO(item);
    }

    @Override
    @Transactional
    public MediaItemDTO updateItem(MediaItemRequest request, Long userId) {
        MediaItem item = mediaItemDao.selectById(request.getId());
        if (item == null) {
            throw new BusinessException(ResponseEnum.DATA_NOT_EXIST);
        }
        if (!item.getUserId().equals(userId)) {
            throw new BusinessException(ResponseEnum.NO_PERMISSION);
        }
        item.setTitle(request.getTitle());
        item.setType(request.getType());
        item.setStatus(request.getStatus());
        item.setRating(request.getRating());
        item.setReview(request.getReview());
        item.setTags(convertTagsToString(request.getTags()));
        item.setCoverUrl(request.getCoverUrl());
        item.setAuthor(request.getAuthor());
        item.setYear(request.getYear());
        mediaItemDao.updateById(item);
        return convertToDTO(item);
    }

    @Override
    @Transactional
    public void deleteItem(Long id, Long userId) {
        MediaItem item = mediaItemDao.selectById(id);
        if (item == null) {
            throw new BusinessException(ResponseEnum.DATA_NOT_EXIST);
        }
        if (!item.getUserId().equals(userId)) {
            throw new BusinessException(ResponseEnum.NO_PERMISSION);
        }
        mediaItemDao.deleteById(id);
    }

    @Override
    public MediaItemDTO getItemById(Long id, Long userId) {
        MediaItem item = mediaItemDao.selectById(id);
        if (item == null) {
            throw new BusinessException(ResponseEnum.DATA_NOT_EXIST);
        }
        if (!item.getUserId().equals(userId)) {
            throw new BusinessException(ResponseEnum.NO_PERMISSION);
        }
        return convertToDTO(item);
    }

    @Override
    public List<MediaItemDTO> listItems(Long userId, Integer type, Integer status) {
        List<MediaItem> items = mediaItemDao.selectByUserIdTypeAndStatus(userId, type, status);
        return items.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<MediaItemDTO> getRecommendations(Long userId, Integer limit) {
        List<MediaItem> userItems = mediaItemDao.selectByUserId(userId);
        
        List<String> userTags = extractAllTags(userItems);
        
        List<Long> excludeIds = userItems.stream()
                .map(MediaItem::getId)
                .collect(Collectors.toList());

        int recLimit = limit != null ? limit : RECOMMENDATION_LIMIT;
        List<MediaItem> recommendations = mediaItemDao.selectRecommendations(userId, userTags, excludeIds, recLimit);

        if (CollectionUtils.isEmpty(recommendations) && !CollectionUtils.isEmpty(userTags)) {
            recommendations = mediaItemDao.selectRecommendations(userId, null, excludeIds, recLimit);
        }

        return recommendations.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<String> getAllTags(Long userId) {
        List<String> tagStrings = mediaItemDao.selectAllTagsByUserId(userId);
        Set<String> allTags = new HashSet<>();
        for (String tags : tagStrings) {
            if (StringUtils.hasText(tags)) {
                String[] tagArray = tags.split(",");
                allTags.addAll(Arrays.asList(tagArray));
            }
        }
        return new ArrayList<>(allTags);
    }

    @Override
    @Transactional
    public Map<String, Object> createShareLink(Long userId, ShareRequest request) {
        ShareLink shareLink = new ShareLink();
        shareLink.setUserId(userId);
        shareLink.setShareCode(RandomUtil.getRandomAlphanumeric(16));
        shareLink.setTitle(request.getTitle());
        shareLink.setExpireTime(request.getExpireTime());
        shareLinkDao.insert(shareLink);
        
        Map<String, Object> result = new HashMap<>();
        result.put("shareCode", shareLink.getShareCode());
        result.put("shareUrl", "/share/" + shareLink.getShareCode());
        result.put("title", shareLink.getTitle());
        result.put("expireTime", shareLink.getExpireTime());
        return result;
    }

    @Override
    public Map<String, Object> getSharedItems(String shareCode) {
        ShareLink shareLink = shareLinkDao.selectByShareCode(shareCode);
        if (shareLink == null) {
            throw new BusinessException(ResponseEnum.DATA_NOT_EXIST);
        }
        
        Long now = System.currentTimeMillis();
        if (shareLink.getExpireTime() != null && shareLink.getExpireTime() < now) {
            throw new BusinessException(ResponseEnum.SHARE_EXPIRED);
        }

        List<MediaItem> items = mediaItemDao.selectByUserId(shareLink.getUserId());
        List<MediaItemDTO> dtoList = items.stream().map(this::convertToDTO).collect(Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("title", shareLink.getTitle());
        result.put("items", dtoList);
        return result;
    }

    @Override
    @Transactional
    public void deleteShareLink(Long userId, String shareCode) {
        ShareLink shareLink = shareLinkDao.selectByShareCode(shareCode);
        if (shareLink == null) {
            throw new BusinessException(ResponseEnum.DATA_NOT_EXIST);
        }
        if (!shareLink.getUserId().equals(userId)) {
            throw new BusinessException(ResponseEnum.NO_PERMISSION);
        }
        shareLinkDao.deleteById(shareLink.getId());
    }

    @Override
    public List<Map<String, Object>> listShareLinks(Long userId) {
        List<ShareLink> links = shareLinkDao.selectByUserId(userId);
        return links.stream().map(link -> {
            Map<String, Object> map = new HashMap<>();
            map.put("shareCode", link.getShareCode());
            map.put("shareUrl", "/share/" + link.getShareCode());
            map.put("title", link.getTitle());
            map.put("expireTime", link.getExpireTime());
            map.put("createTime", link.getCreateTime());
            return map;
        }).collect(Collectors.toList());
    }

    private MediaItemDTO convertToDTO(MediaItem item) {
        MediaItemDTO dto = new MediaItemDTO();
        dto.setId(item.getId());
        dto.setTitle(item.getTitle());
        dto.setType(item.getType());
        dto.setStatus(item.getStatus());
        dto.setRating(item.getRating());
        dto.setReview(item.getReview());
        dto.setTags(convertStringToTags(item.getTags()));
        dto.setCoverUrl(item.getCoverUrl());
        dto.setAuthor(item.getAuthor());
        dto.setYear(item.getYear());
        dto.setCreateTime(item.getCreateTime());
        dto.setUpdateTime(item.getUpdateTime());
        return dto;
    }

    private String convertTagsToString(List<String> tags) {
        if (CollectionUtils.isEmpty(tags)) {
            return null;
        }
        return String.join(",", tags);
    }

    private List<String> convertStringToTags(String tags) {
        if (!StringUtils.hasText(tags)) {
            return new ArrayList<>();
        }
        return Arrays.asList(tags.split(","));
    }

    private List<String> extractAllTags(List<MediaItem> items) {
        Set<String> tagSet = new HashSet<>();
        for (MediaItem item : items) {
            if (StringUtils.hasText(item.getTags())) {
                String[] tags = item.getTags().split(",");
                tagSet.addAll(Arrays.asList(tags));
            }
        }
        return new ArrayList<>(tagSet);
    }
}
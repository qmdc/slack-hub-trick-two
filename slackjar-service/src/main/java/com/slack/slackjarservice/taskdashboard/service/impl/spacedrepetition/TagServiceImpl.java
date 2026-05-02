package com.slack.slackjarservice.taskdashboard.service.impl.spacedrepetition;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.slack.slackjarservice.common.enumtype.foundation.ResponseEnum;
import com.slack.slackjarservice.common.util.AssertUtil;
import com.slack.slackjarservice.taskdashboard.dao.spacedrepetition.CardTagDao;
import com.slack.slackjarservice.taskdashboard.dao.spacedrepetition.TagDao;
import com.slack.slackjarservice.taskdashboard.entity.spacedrepetition.CardTag;
import com.slack.slackjarservice.taskdashboard.entity.spacedrepetition.Tag;
import com.slack.slackjarservice.taskdashboard.model.dto.spacedrepetition.TagDTO;
import com.slack.slackjarservice.taskdashboard.model.request.spacedrepetition.CreateTagRequest;
import com.slack.slackjarservice.taskdashboard.service.spacedrepetition.TagService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TagServiceImpl extends ServiceImpl<TagDao, Tag> implements TagService {

    @Resource
    private CardTagDao cardTagDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TagDTO createTag(CreateTagRequest request, Long userId) {
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Tag::getUserId, userId)
                .eq(Tag::getName, request.getName());
        Tag existTag = this.getOne(queryWrapper);
        if (existTag != null) {
            return convertToDTO(existTag);
        }

        Tag tag = new Tag();
        BeanUtils.copyProperties(request, tag);
        tag.setUserId(userId);
        tag.setCardCount(0);
        if (tag.getSortOrder() == null) {
            tag.setSortOrder(getNextSortOrder(userId));
        }

        this.save(tag);

        return convertToDTO(tag);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TagDTO updateTag(Long tagId, CreateTagRequest request, Long userId) {
        Tag tag = getById(tagId);
        AssertUtil.notNull(tag, ResponseEnum.DATA_NOT_EXISTS);
        AssertUtil.isTrue(Objects.equals(tag.getUserId(), userId), ResponseEnum.NO_PERMISSION);

        if (request.getName() != null) {
            tag.setName(request.getName());
        }
        if (request.getColor() != null) {
            tag.setColor(request.getColor());
        }
        if (request.getSortOrder() != null) {
            tag.setSortOrder(request.getSortOrder());
        }

        this.updateById(tag);

        return convertToDTO(getById(tagId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTag(Long tagId, Long userId) {
        Tag tag = getById(tagId);
        AssertUtil.notNull(tag, ResponseEnum.DATA_NOT_EXISTS);
        AssertUtil.isTrue(Objects.equals(tag.getUserId(), userId), ResponseEnum.NO_PERMISSION);

        LambdaQueryWrapper<CardTag> cardTagWrapper = new LambdaQueryWrapper<>();
        cardTagWrapper.eq(CardTag::getTagId, tagId);
        cardTagDao.delete(cardTagWrapper);

        this.removeById(tagId);
    }

    @Override
    public List<TagDTO> getUserTags(Long userId) {
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Tag::getUserId, userId);
        queryWrapper.orderByAsc(Tag::getSortOrder).orderByDesc(Tag::getCreateTime);

        List<Tag> tags = this.list(queryWrapper);
        return convertToDTOList(tags);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addTagsToCard(Long cardId, List<Long> tagIds, Long userId) {
        if (CollectionUtils.isEmpty(tagIds)) {
            return;
        }

        for (Long tagId : tagIds) {
            LambdaQueryWrapper<CardTag> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(CardTag::getCardId, cardId)
                    .eq(CardTag::getTagId, tagId);
            CardTag exist = cardTagDao.selectOne(queryWrapper);

            if (exist == null) {
                CardTag cardTag = new CardTag();
                cardTag.setCardId(cardId);
                cardTag.setTagId(tagId);
                cardTagDao.insert(cardTag);

                updateTagCardCount(tagId, 1);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeTagFromCard(Long cardId, Long tagId, Long userId) {
        LambdaQueryWrapper<CardTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CardTag::getCardId, cardId)
                .eq(CardTag::getTagId, tagId);
        cardTagDao.delete(queryWrapper);

        updateTagCardCount(tagId, -1);
    }

    @Override
    public List<TagDTO> getCardTags(Long cardId) {
        if (cardId == null) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<CardTag> cardTagWrapper = new LambdaQueryWrapper<>();
        cardTagWrapper.eq(CardTag::getCardId, cardId);
        List<CardTag> cardTags = cardTagDao.selectList(cardTagWrapper);

        if (CollectionUtils.isEmpty(cardTags)) {
            return new ArrayList<>();
        }

        List<Long> tagIds = cardTags.stream()
                .map(CardTag::getTagId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (tagIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Tag> tags = this.listByIds(tagIds);
        return convertToDTOList(tags);
    }

    @Override
    public TagDTO convertToDTO(Tag tag) {
        if (tag == null) {
            return null;
        }

        TagDTO dto = new TagDTO();
        BeanUtils.copyProperties(tag, dto);
        return dto;
    }

    @Override
    public List<TagDTO> convertToDTOList(List<Tag> tags) {
        if (CollectionUtils.isEmpty(tags)) {
            return new ArrayList<>();
        }

        List<TagDTO> result = new ArrayList<>();
        for (Tag tag : tags) {
            result.add(convertToDTO(tag));
        }
        return result;
    }

    private Integer getNextSortOrder(Long userId) {
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Tag::getUserId, userId);
        queryWrapper.orderByDesc(Tag::getSortOrder);
        queryWrapper.last("LIMIT 1");

        Tag lastTag = this.getOne(queryWrapper);
        if (lastTag != null && lastTag.getSortOrder() != null) {
            return lastTag.getSortOrder() + 1;
        }
        return 1;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateTagCardCount(Long tagId, int delta) {
        Tag tag = getById(tagId);
        if (tag != null) {
            int newCount = (tag.getCardCount() != null ? tag.getCardCount() : 0) + delta;
            tag.setCardCount(Math.max(0, newCount));
            this.updateById(tag);
        }
    }
}

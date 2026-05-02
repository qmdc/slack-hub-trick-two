package com.slack.slackjarservice.taskdashboard.service.impl.spacedrepetition;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.slack.slackjarservice.common.enumtype.foundation.ResponseEnum;
import com.slack.slackjarservice.common.enumtype.taskdashboard.spacedrepetition.MasteryLevelEnum;
import com.slack.slackjarservice.common.enumtype.taskdashboard.spacedrepetition.ReviewRatingEnum;
import com.slack.slackjarservice.common.response.PageResult;
import com.slack.slackjarservice.common.util.AssertUtil;
import com.slack.slackjarservice.taskdashboard.dao.spacedrepetition.*;
import com.slack.slackjarservice.taskdashboard.entity.spacedrepetition.*;
import com.slack.slackjarservice.taskdashboard.model.dto.spacedrepetition.CardDTO;
import com.slack.slackjarservice.taskdashboard.model.dto.spacedrepetition.ReviewResultDTO;
import com.slack.slackjarservice.taskdashboard.model.dto.spacedrepetition.TagDTO;
import com.slack.slackjarservice.taskdashboard.model.request.spacedrepetition.CardPageQuery;
import com.slack.slackjarservice.taskdashboard.model.request.spacedrepetition.CreateCardRequest;
import com.slack.slackjarservice.taskdashboard.model.request.spacedrepetition.ReviewCardRequest;
import com.slack.slackjarservice.taskdashboard.model.request.spacedrepetition.UpdateCardRequest;
import com.slack.slackjarservice.taskdashboard.service.spacedrepetition.CardService;
import com.slack.slackjarservice.taskdashboard.service.spacedrepetition.DeckService;
import com.slack.slackjarservice.taskdashboard.util.SpacedRepetitionAlgorithm;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CardServiceImpl extends ServiceImpl<CardDao, Card> implements CardService {

    @Resource
    private DeckService deckService;

    @Resource
    private CardTagDao cardTagDao;

    @Resource
    private TagDao tagDao;

    @Resource
    private ReviewRecordDao reviewRecordDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CardDTO createCard(CreateCardRequest request, Long userId) {
        Card card = new Card();
        BeanUtils.copyProperties(request, card);
        card.setUserId(userId);

        if (request.getFrontImageIds() != null && !request.getFrontImageIds().isEmpty()) {
            card.setFrontImageIds(JSON.toJSONString(request.getFrontImageIds()));
        }
        if (request.getBackImageIds() != null && !request.getBackImageIds().isEmpty()) {
            card.setBackImageIds(JSON.toJSONString(request.getBackImageIds()));
        }

        SpacedRepetitionAlgorithm.initializeNewCard(card);

        if (card.getSortOrder() == null) {
            card.setSortOrder(getNextSortOrder(request.getDeckId()));
        }

        this.save(card);

        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            for (Long tagId : request.getTagIds()) {
                CardTag cardTag = new CardTag();
                cardTag.setCardId(card.getId());
                cardTag.setTagId(tagId);
                cardTagDao.insert(cardTag);
            }
        }

        deckService.updateMasteryRate(card.getDeckId());
        deckService.updateTodayReviewCount(card.getDeckId());

        return convertToDTO(getById(card.getId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CardDTO updateCard(Long cardId, UpdateCardRequest request, Long userId) {
        Card card = getById(cardId);
        AssertUtil.notNull(card, ResponseEnum.DATA_NOT_EXISTS);
        AssertUtil.isTrue(Objects.equals(card.getUserId(), userId), ResponseEnum.NO_PERMISSION);

        Long originalDeckId = card.getDeckId();

        if (StringUtils.hasText(request.getFrontContent())) {
            card.setFrontContent(request.getFrontContent());
        }
        if (request.getFrontContent() != null) {
            card.setFrontContent(request.getFrontContent());
        }
        if (StringUtils.hasText(request.getBackContent())) {
            card.setBackContent(request.getBackContent());
        }
        if (request.getBackContent() != null) {
            card.setBackContent(request.getBackContent());
        }
        if (request.getDeckId() != null) {
            card.setDeckId(request.getDeckId());
        }
        if (request.getFrontImageIds() != null) {
            if (request.getFrontImageIds().isEmpty()) {
                card.setFrontImageIds(null);
            } else {
                card.setFrontImageIds(JSON.toJSONString(request.getFrontImageIds()));
            }
        }
        if (request.getBackImageIds() != null) {
            if (request.getBackImageIds().isEmpty()) {
                card.setBackImageIds(null);
            } else {
                card.setBackImageIds(JSON.toJSONString(request.getBackImageIds()));
            }
        }
        if (request.getIsImportant() != null) {
            card.setIsImportant(request.getIsImportant());
        }
        if (request.getSortOrder() != null) {
            card.setSortOrder(request.getSortOrder());
        }

        this.updateById(card);

        if (request.getTagIds() != null) {
            LambdaQueryWrapper<CardTag> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(CardTag::getCardId, cardId);
            cardTagDao.delete(deleteWrapper);

            for (Long tagId : request.getTagIds()) {
                CardTag cardTag = new CardTag();
                cardTag.setCardId(cardId);
                cardTag.setTagId(tagId);
                cardTagDao.insert(cardTag);
            }
        }

        if (!Objects.equals(originalDeckId, card.getDeckId())) {
            deckService.updateMasteryRate(originalDeckId);
            deckService.updateTodayReviewCount(originalDeckId);
            deckService.updateMasteryRate(card.getDeckId());
            deckService.updateTodayReviewCount(card.getDeckId());
        }

        return convertToDTO(getById(cardId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCard(Long cardId, Long userId) {
        Card card = getById(cardId);
        AssertUtil.notNull(card, ResponseEnum.DATA_NOT_EXISTS);
        AssertUtil.isTrue(Objects.equals(card.getUserId(), userId), ResponseEnum.NO_PERMISSION);

        LambdaQueryWrapper<CardTag> cardTagWrapper = new LambdaQueryWrapper<>();
        cardTagWrapper.eq(CardTag::getCardId, cardId);
        cardTagDao.delete(cardTagWrapper);

        this.removeById(cardId);

        deckService.updateMasteryRate(card.getDeckId());
        deckService.updateTodayReviewCount(card.getDeckId());
    }

    @Override
    public CardDTO getCardById(Long cardId, Long userId) {
        Card card = getById(cardId);
        AssertUtil.notNull(card, ResponseEnum.DATA_NOT_EXISTS);
        AssertUtil.isTrue(Objects.equals(card.getUserId(), userId), ResponseEnum.NO_PERMISSION);

        return convertToDTO(card);
    }

    @Override
    public PageResult<CardDTO> pageQueryCards(CardPageQuery query, Long userId) {
        LambdaQueryWrapper<Card> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Card::getUserId, userId);

        if (query.getDeckId() != null) {
            queryWrapper.eq(Card::getDeckId, query.getDeckId());
        }

        if (StringUtils.hasText(query.getKeyword())) {
            queryWrapper.and(w -> w
                    .like(Card::getFrontContent, query.getKeyword())
                    .or()
                    .like(Card::getBackContent, query.getKeyword()));
        }

        if (query.getMasteryLevel() != null) {
            queryWrapper.eq(Card::getMasteryLevel, query.getMasteryLevel());
        }

        if (query.getIsImportant() != null) {
            queryWrapper.eq(Card::getIsImportant, query.getIsImportant());
        }

        if (Boolean.TRUE.equals(query.getIsDueToday())) {
            long todayStart = getTodayStartMillis();
            queryWrapper.le(Card::getNextReviewTime, todayStart + SpacedRepetitionAlgorithm.DAY_IN_MILLIS);
        }

        queryWrapper.orderByAsc(Card::getNextReviewTime)
                .orderByAsc(Card::getSortOrder)
                .orderByDesc(Card::getCreateTime);

        Page<Card> cardPage = this.page(
                new Page<>(query.getPageNo(), query.getPageSize()),
                queryWrapper
        );

        List<CardDTO> cardItems = convertToDTOList(cardPage.getRecords());

        return PageResult.of(cardItems, cardPage.getTotal(), query.getPageNo(), query.getPageSize());
    }

    @Override
    public List<CardDTO> getTodayReviewCards(Long userId, Long deckId, Integer limit) {
        LambdaQueryWrapper<Card> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Card::getUserId, userId);

        if (deckId != null) {
            queryWrapper.eq(Card::getDeckId, deckId);
        }

        long todayStart = getTodayStartMillis();
        queryWrapper.le(Card::getNextReviewTime, todayStart + SpacedRepetitionAlgorithm.DAY_IN_MILLIS);

        queryWrapper.orderByAsc(Card::getNextReviewTime)
                .orderByAsc(Card::getSortOrder);

        if (limit != null && limit > 0) {
            queryWrapper.last("LIMIT " + limit);
        }

        List<Card> cards = this.list(queryWrapper);
        return convertToDTOList(cards);
    }

    @Override
    public List<CardDTO> getNewCards(Long userId, Long deckId, Integer limit) {
        LambdaQueryWrapper<Card> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Card::getUserId, userId);
        queryWrapper.eq(Card::getReviewCount, 0);

        if (deckId != null) {
            queryWrapper.eq(Card::getDeckId, deckId);
        }

        queryWrapper.orderByAsc(Card::getSortOrder).orderByAsc(Card::getCreateTime);

        if (limit != null && limit > 0) {
            queryWrapper.last("LIMIT " + limit);
        }

        List<Card> cards = this.list(queryWrapper);
        return convertToDTOList(cards);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReviewResultDTO reviewCard(ReviewCardRequest request, Long userId) {
        Card card = getById(request.getCardId());
        AssertUtil.notNull(card, ResponseEnum.DATA_NOT_EXISTS);
        AssertUtil.isTrue(Objects.equals(card.getUserId(), userId), ResponseEnum.NO_PERMISSION);

        BigDecimal previousInterval = card.getIntervalDays() != null ? card.getIntervalDays() : BigDecimal.ZERO;
        BigDecimal previousEaseFactor = card.getEaseFactor() != null ? card.getEaseFactor() : SpacedRepetitionAlgorithm.INITIAL_EASE_FACTOR;
        int reviewCount = card.getReviewCount() != null ? card.getReviewCount() : 0;

        SpacedRepetitionAlgorithm.AlgorithmResult result = SpacedRepetitionAlgorithm.calculateNextReview(
                previousInterval,
                previousEaseFactor,
                reviewCount,
                request.getRating()
        );

        boolean isCorrect = result.getIsCorrect();
        card.setIntervalDays(result.getNewInterval());
        card.setEaseFactor(result.getNewEaseFactor());
        card.setNextReviewTime(result.getNextReviewTime());
        card.setMasteryLevel(result.getMasteryLevel());
        card.setLastReviewTime(System.currentTimeMillis());
        card.setReviewCount(reviewCount + 1);

        if (isCorrect) {
            card.setCorrectCount((card.getCorrectCount() != null ? card.getCorrectCount() : 0) + 1);
            card.setConsecutiveCorrectCount((card.getConsecutiveCorrectCount() != null ? card.getConsecutiveCorrectCount() : 0) + 1);
        } else {
            card.setIncorrectCount((card.getIncorrectCount() != null ? card.getIncorrectCount() : 0) + 1);
            card.setConsecutiveCorrectCount(0);
        }

        this.updateById(card);

        ReviewRecord reviewRecord = new ReviewRecord();
        reviewRecord.setUserId(userId);
        reviewRecord.setCardId(card.getId());
        reviewRecord.setDeckId(card.getDeckId());
        reviewRecord.setRating(request.getRating());
        reviewRecord.setPreviousInterval(previousInterval);
        reviewRecord.setNewInterval(result.getNewInterval());
        reviewRecord.setPreviousEaseFactor(previousEaseFactor);
        reviewRecord.setNewEaseFactor(result.getNewEaseFactor());
        reviewRecord.setReviewDuration(request.getReviewDuration());
        reviewRecord.setReviewTime(System.currentTimeMillis());
        reviewRecord.setIsCorrect(isCorrect ? 1 : 0);
        reviewRecordDao.insert(reviewRecord);

        deckService.updateMasteryRate(card.getDeckId());
        deckService.updateTodayReviewCount(card.getDeckId());

        ReviewResultDTO resultDTO = new ReviewResultDTO();
        resultDTO.setCardId(card.getId());
        resultDTO.setRating(request.getRating());
        resultDTO.setRatingDesc(ReviewRatingEnum.getDescByCode(request.getRating()));
        resultDTO.setPreviousInterval(previousInterval);
        resultDTO.setNewInterval(result.getNewInterval());
        resultDTO.setPreviousEaseFactor(previousEaseFactor);
        resultDTO.setNewEaseFactor(result.getNewEaseFactor());
        resultDTO.setNextReviewTime(result.getNextReviewTime());
        resultDTO.setMasteryLevel(result.getMasteryLevel());
        resultDTO.setMasteryLevelDesc(MasteryLevelEnum.getDescByCode(result.getMasteryLevel()));
        resultDTO.setIsCorrect(isCorrect);
        resultDTO.setReviewDuration(request.getReviewDuration());

        return resultDTO;
    }

    @Override
    public int countCardsByDeckId(Long deckId) {
        LambdaQueryWrapper<Card> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Card::getDeckId, deckId);
        return Math.toIntExact(this.count(queryWrapper));
    }

    @Override
    public int countTodayReviewCards(Long userId, Long deckId) {
        LambdaQueryWrapper<Card> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Card::getUserId, userId);

        if (deckId != null) {
            queryWrapper.eq(Card::getDeckId, deckId);
        }

        long todayStart = getTodayStartMillis();
        queryWrapper.le(Card::getNextReviewTime, todayStart + SpacedRepetitionAlgorithm.DAY_IN_MILLIS);

        return Math.toIntExact(this.count(queryWrapper));
    }

    @Override
    public CardDTO convertToDTO(Card card) {
        if (card == null) {
            return null;
        }

        CardDTO dto = new CardDTO();
        BeanUtils.copyProperties(card, dto);

        if (StringUtils.hasText(card.getFrontImageIds())) {
            try {
                dto.setFrontImageIds(JSON.parseArray(card.getFrontImageIds(), Long.class));
            } catch (Exception e) {
                log.warn("Failed to parse frontImageIds: {}", card.getFrontImageIds());
            }
        }
        if (StringUtils.hasText(card.getBackImageIds())) {
            try {
                dto.setBackImageIds(JSON.parseArray(card.getBackImageIds(), Long.class));
            } catch (Exception e) {
                log.warn("Failed to parse backImageIds: {}", card.getBackImageIds());
            }
        }

        dto.setMasteryLevelDesc(MasteryLevelEnum.getDescByCode(card.getMasteryLevel()));
        dto.setIsDueToday(SpacedRepetitionAlgorithm.isCardDueToday(card.getNextReviewTime()));

        List<Tag> tags = getTagsForCard(card.getId());
        dto.setTags(convertTagsToDTO(tags));

        return dto;
    }

    @Override
    public List<CardDTO> convertToDTOList(List<Card> cards) {
        if (CollectionUtils.isEmpty(cards)) {
            return new ArrayList<>();
        }

        Set<Long> cardIds = cards.stream()
                .map(Card::getId)
                .collect(Collectors.toSet());

        Map<Long, List<Tag>> cardTagsMap = new HashMap<>();
        if (!cardIds.isEmpty()) {
            LambdaQueryWrapper<CardTag> cardTagWrapper = new LambdaQueryWrapper<>();
            cardTagWrapper.in(CardTag::getCardId, cardIds);
            List<CardTag> cardTags = cardTagDao.selectList(cardTagWrapper);

            if (!CollectionUtils.isEmpty(cardTags)) {
                Set<Long> tagIds = cardTags.stream()
                        .map(CardTag::getTagId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

                Map<Long, Tag> tagMap = new HashMap<>();
                if (!tagIds.isEmpty()) {
                    List<Tag> tags = tagDao.selectBatchIds(tagIds);
                    tagMap = tags.stream().collect(Collectors.toMap(Tag::getId, t -> t));
                }

                for (CardTag cardTag : cardTags) {
                    Tag tag = tagMap.get(cardTag.getTagId());
                    if (tag != null) {
                        cardTagsMap.computeIfAbsent(cardTag.getCardId(), k -> new ArrayList<>()).add(tag);
                    }
                }
            }
        }

        List<CardDTO> result = new ArrayList<>();
        for (Card card : cards) {
            CardDTO dto = new CardDTO();
            BeanUtils.copyProperties(card, dto);

            if (StringUtils.hasText(card.getFrontImageIds())) {
                try {
                    dto.setFrontImageIds(JSON.parseArray(card.getFrontImageIds(), Long.class));
                } catch (Exception e) {
                    log.warn("Failed to parse frontImageIds: {}", card.getFrontImageIds());
                }
            }
            if (StringUtils.hasText(card.getBackImageIds())) {
                try {
                    dto.setBackImageIds(JSON.parseArray(card.getBackImageIds(), Long.class));
                } catch (Exception e) {
                    log.warn("Failed to parse backImageIds: {}", card.getBackImageIds());
                }
            }

            dto.setMasteryLevelDesc(MasteryLevelEnum.getDescByCode(card.getMasteryLevel()));
            dto.setIsDueToday(SpacedRepetitionAlgorithm.isCardDueToday(card.getNextReviewTime()));

            List<Tag> tags = cardTagsMap.getOrDefault(card.getId(), new ArrayList<>());
            dto.setTags(convertTagsToDTO(tags));

            result.add(dto);
        }
        return result;
    }

    private List<Tag> getTagsForCard(Long cardId) {
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

        return tagDao.selectBatchIds(tagIds);
    }

    private List<TagDTO> convertTagsToDTO(List<Tag> tags) {
        if (CollectionUtils.isEmpty(tags)) {
            return new ArrayList<>();
        }

        List<TagDTO> result = new ArrayList<>();
        for (Tag tag : tags) {
            TagDTO dto = new TagDTO();
            BeanUtils.copyProperties(tag, dto);
            result.add(dto);
        }
        return result;
    }

    private Integer getNextSortOrder(Long deckId) {
        LambdaQueryWrapper<Card> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Card::getDeckId, deckId);
        queryWrapper.orderByDesc(Card::getSortOrder);
        queryWrapper.last("LIMIT 1");

        Card lastCard = this.getOne(queryWrapper);
        if (lastCard != null && lastCard.getSortOrder() != null) {
            return lastCard.getSortOrder() + 1;
        }
        return 1;
    }

    private long getTodayStartMillis() {
        return System.currentTimeMillis() - (System.currentTimeMillis() % (24 * 60 * 60 * 1000L));
    }
}

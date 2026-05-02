package com.slack.slackjarservice.taskdashboard.service.impl.spacedrepetition;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.slack.slackjarservice.common.enumtype.foundation.ResponseEnum;
import com.slack.slackjarservice.common.response.PageResult;
import com.slack.slackjarservice.common.util.AssertUtil;
import com.slack.slackjarservice.taskdashboard.dao.spacedrepetition.*;
import com.slack.slackjarservice.taskdashboard.entity.spacedrepetition.*;
import com.slack.slackjarservice.taskdashboard.model.dto.spacedrepetition.DeckDTO;
import com.slack.slackjarservice.taskdashboard.model.dto.spacedrepetition.TagDTO;
import com.slack.slackjarservice.taskdashboard.model.request.spacedrepetition.CreateDeckRequest;
import com.slack.slackjarservice.taskdashboard.model.request.spacedrepetition.DeckPageQuery;
import com.slack.slackjarservice.taskdashboard.model.request.spacedrepetition.UpdateDeckRequest;
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
public class DeckServiceImpl extends ServiceImpl<DeckDao, Deck> implements DeckService {

    @Resource
    private CardDao cardDao;

    @Resource
    private CardTagDao cardTagDao;

    @Resource
    private TagDao tagDao;

    @Resource
    private MarketDeckDao marketDeckDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeckDTO createDeck(CreateDeckRequest request, Long userId) {
        Deck deck = new Deck();
        BeanUtils.copyProperties(request, deck);
        deck.setUserId(userId);
        deck.setCardCount(0);
        deck.setTodayReviewCount(0);
        deck.setMasteryRate(BigDecimal.ZERO);
        deck.setIsPublic(0);
        if (deck.getSortOrder() == null) {
            deck.setSortOrder(getNextSortOrder(userId));
        }

        this.save(deck);

        return convertToDTO(deck);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeckDTO updateDeck(Long deckId, UpdateDeckRequest request, Long userId) {
        Deck deck = getById(deckId);
        AssertUtil.notNull(deck, ResponseEnum.DATA_NOT_EXISTS);
        AssertUtil.isTrue(Objects.equals(deck.getUserId(), userId), ResponseEnum.NO_PERMISSION);

        if (StringUtils.hasText(request.getName())) {
            deck.setName(request.getName());
        }
        if (request.getDescription() != null) {
            deck.setDescription(request.getDescription());
        }
        if (request.getCoverImageId() != null) {
            deck.setCoverImageId(request.getCoverImageId());
        }
        if (request.getCoverImageUrl() != null) {
            deck.setCoverImageUrl(request.getCoverImageUrl());
        }
        if (request.getIsPublic() != null) {
            deck.setIsPublic(request.getIsPublic());
        }
        if (request.getSortOrder() != null) {
            deck.setSortOrder(request.getSortOrder());
        }

        this.updateById(deck);

        return convertToDTO(getById(deckId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDeck(Long deckId, Long userId) {
        Deck deck = getById(deckId);
        AssertUtil.notNull(deck, ResponseEnum.DATA_NOT_EXISTS);
        AssertUtil.isTrue(Objects.equals(deck.getUserId(), userId), ResponseEnum.NO_PERMISSION);

        LambdaQueryWrapper<Card> cardWrapper = new LambdaQueryWrapper<>();
        cardWrapper.eq(Card::getDeckId, deckId);
        List<Card> cards = cardDao.selectList(cardWrapper);
        if (!CollectionUtils.isEmpty(cards)) {
            List<Long> cardIds = cards.stream().map(Card::getId).collect(Collectors.toList());
            LambdaQueryWrapper<CardTag> cardTagWrapper = new LambdaQueryWrapper<>();
            cardTagWrapper.in(CardTag::getCardId, cardIds);
            cardTagDao.delete(cardTagWrapper);
            cardDao.deleteByIds(cardIds);
        }

        this.removeById(deckId);
    }

    @Override
    public DeckDTO getDeckById(Long deckId, Long userId) {
        Deck deck = getById(deckId);
        AssertUtil.notNull(deck, ResponseEnum.DATA_NOT_EXISTS);
        
        boolean hasPermission = Objects.equals(deck.getUserId(), userId) || Objects.equals(deck.getIsPublic(), 1);
        AssertUtil.isTrue(hasPermission, ResponseEnum.NO_PERMISSION);

        return convertToDTO(deck);
    }

    @Override
    public PageResult<DeckDTO> pageQueryDecks(DeckPageQuery query, Long userId) {
        LambdaQueryWrapper<Deck> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Deck::getUserId, userId);

        if (StringUtils.hasText(query.getKeyword())) {
            queryWrapper.and(w -> w
                    .like(Deck::getName, query.getKeyword())
                    .or()
                    .like(Deck::getDescription, query.getKeyword()));
        }

        if (query.getIsPublic() != null) {
            queryWrapper.eq(Deck::getIsPublic, query.getIsPublic());
        }

        queryWrapper.orderByAsc(Deck::getSortOrder).orderByDesc(Deck::getCreateTime);

        Page<Deck> deckPage = this.page(
                new Page<>(query.getPageNo(), query.getPageSize()),
                queryWrapper
        );

        List<DeckDTO> deckItems = convertToDTOList(deckPage.getRecords());

        return PageResult.of(deckItems, deckPage.getTotal(), query.getPageNo(), query.getPageSize());
    }

    @Override
    public List<DeckDTO> getUserDecks(Long userId) {
        LambdaQueryWrapper<Deck> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Deck::getUserId, userId);
        queryWrapper.orderByAsc(Deck::getSortOrder).orderByDesc(Deck::getCreateTime);

        List<Deck> decks = this.list(queryWrapper);
        return convertToDTOList(decks);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTodayReviewCount(Long deckId) {
        LambdaQueryWrapper<Card> cardWrapper = new LambdaQueryWrapper<>();
        cardWrapper.eq(Card::getDeckId, deckId);

        List<Card> cards = cardDao.selectList(cardWrapper);
        int todayReviewCount = 0;
        for (Card card : cards) {
            if (SpacedRepetitionAlgorithm.isCardDueToday(card.getNextReviewTime())) {
                todayReviewCount++;
            }
        }

        Deck deck = getById(deckId);
        if (deck != null) {
            deck.setTodayReviewCount(todayReviewCount);
            this.updateById(deck);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMasteryRate(Long deckId) {
        LambdaQueryWrapper<Card> cardWrapper = new LambdaQueryWrapper<>();
        cardWrapper.eq(Card::getDeckId, deckId);

        List<Card> cards = cardDao.selectList(cardWrapper);
        if (CollectionUtils.isEmpty(cards)) {
            Deck deck = getById(deckId);
            if (deck != null) {
                deck.setMasteryRate(BigDecimal.ZERO);
                deck.setCardCount(0);
                this.updateById(deck);
            }
            return;
        }

        int masteredCards = 0;
        for (Card card : cards) {
            if (card.getMasteryLevel() != null && card.getMasteryLevel() >= 3) {
                masteredCards++;
            }
        }

        BigDecimal masteryRate = new BigDecimal(masteredCards)
                .multiply(new BigDecimal(100))
                .divide(new BigDecimal(cards.size()), 2, java.math.RoundingMode.HALF_UP);

        Deck deck = getById(deckId);
        if (deck != null) {
            deck.setMasteryRate(masteryRate);
            deck.setCardCount(cards.size());
            this.updateById(deck);
        }
    }

    @Override
    public DeckDTO convertToDTO(Deck deck) {
        if (deck == null) {
            return null;
        }

        DeckDTO dto = new DeckDTO();
        BeanUtils.copyProperties(deck, dto);

        List<Tag> tags = getTagsForDeck(deck.getId(), deck.getUserId());
        dto.setTags(convertTagsToDTO(tags));

        return dto;
    }

    @Override
    public List<DeckDTO> convertToDTOList(List<Deck> decks) {
        if (CollectionUtils.isEmpty(decks)) {
            return new ArrayList<>();
        }

        List<DeckDTO> result = new ArrayList<>();
        for (Deck deck : decks) {
            result.add(convertToDTO(deck));
        }
        return result;
    }

    private List<Tag> getTagsForDeck(Long deckId, Long userId) {
        LambdaQueryWrapper<Card> cardWrapper = new LambdaQueryWrapper<>();
        cardWrapper.eq(Card::getDeckId, deckId);
        List<Card> cards = cardDao.selectList(cardWrapper);

        if (CollectionUtils.isEmpty(cards)) {
            return new ArrayList<>();
        }

        List<Long> cardIds = cards.stream().map(Card::getId).collect(Collectors.toList());
        LambdaQueryWrapper<CardTag> cardTagWrapper = new LambdaQueryWrapper<>();
        cardTagWrapper.in(CardTag::getCardId, cardIds);
        List<CardTag> cardTags = cardTagDao.selectList(cardTagWrapper);

        if (CollectionUtils.isEmpty(cardTags)) {
            return new ArrayList<>();
        }

        Set<Long> tagIds = cardTags.stream()
                .map(CardTag::getTagId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

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

    private Integer getNextSortOrder(Long userId) {
        LambdaQueryWrapper<Deck> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Deck::getUserId, userId);
        queryWrapper.orderByDesc(Deck::getSortOrder);
        queryWrapper.last("LIMIT 1");

        Deck lastDeck = this.getOne(queryWrapper);
        if (lastDeck != null && lastDeck.getSortOrder() != null) {
            return lastDeck.getSortOrder() + 1;
        }
        return 1;
    }
}

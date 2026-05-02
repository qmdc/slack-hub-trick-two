package com.slack.slackjarservice.taskdashboard.service.impl.spacedrepetition;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.slack.slackjarservice.common.enumtype.foundation.ResponseEnum;
import com.slack.slackjarservice.common.enumtype.taskdashboard.spacedrepetition.MarketDeckStatusEnum;
import com.slack.slackjarservice.common.response.PageResult;
import com.slack.slackjarservice.common.util.AssertUtil;
import com.slack.slackjarservice.taskdashboard.dao.spacedrepetition.*;
import com.slack.slackjarservice.taskdashboard.entity.spacedrepetition.*;
import com.slack.slackjarservice.taskdashboard.model.dto.spacedrepetition.CardDTO;
import com.slack.slackjarservice.taskdashboard.model.dto.spacedrepetition.MarketDeckDTO;
import com.slack.slackjarservice.taskdashboard.model.request.spacedrepetition.MarketDeckPageQuery;
import com.slack.slackjarservice.taskdashboard.model.request.spacedrepetition.ShareToMarketRequest;
import com.slack.slackjarservice.taskdashboard.service.spacedrepetition.MarketDeckService;
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
public class MarketDeckServiceImpl extends ServiceImpl<MarketDeckDao, MarketDeck> implements MarketDeckService {

    @Resource
    private DeckDao deckDao;

    @Resource
    private CardDao cardDao;

    @Resource
    private MarketDeckLikeDao marketDeckLikeDao;

    @Resource
    private MarketDeckFavoriteDao marketDeckFavoriteDao;

    @Resource
    private MarketDeckDownloadDao marketDeckDownloadDao;

    @Resource
    private TagDao tagDao;

    @Resource
    private CardTagDao cardTagDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MarketDeckDTO shareToMarket(ShareToMarketRequest request, Long userId) {
        Long deckId = request.getDeckId();
        Deck deck = deckDao.selectById(deckId);
        AssertUtil.notNull(deck, ResponseEnum.DATA_NOT_EXISTS);
        AssertUtil.isTrue(Objects.equals(deck.getUserId(), userId), ResponseEnum.NO_PERMISSION);

        LambdaQueryWrapper<MarketDeck> existWrapper = new LambdaQueryWrapper<>();
        existWrapper.eq(MarketDeck::getOriginalDeckId, deckId)
                .eq(MarketDeck::getUserId, userId);
        MarketDeck existDeck = this.getOne(existWrapper);
        if (existDeck != null) {
            return convertToDTO(existDeck, userId);
        }

        LambdaQueryWrapper<Card> cardWrapper = new LambdaQueryWrapper<>();
        cardWrapper.eq(Card::getDeckId, deckId);
        int cardCount = Math.toIntExact(cardDao.selectCount(cardWrapper));

        MarketDeck marketDeck = new MarketDeck();
        marketDeck.setOriginalDeckId(deckId);
        marketDeck.setUserId(userId);

        if (StringUtils.hasText(request.getName())) {
            marketDeck.setName(request.getName());
        } else {
            marketDeck.setName(deck.getName());
        }

        if (request.getDescription() != null) {
            marketDeck.setDescription(request.getDescription());
        } else {
            marketDeck.setDescription(deck.getDescription());
        }

        marketDeck.setCoverImageUrl(deck.getCoverImageUrl());
        marketDeck.setCardCount(cardCount);

        if (!CollectionUtils.isEmpty(request.getTags())) {
            marketDeck.setTags(JSON.toJSONString(request.getTags()));
        } else {
            List<String> deckTags = getDeckTags(deckId);
            if (!CollectionUtils.isEmpty(deckTags)) {
                marketDeck.setTags(JSON.toJSONString(deckTags));
            }
        }

        marketDeck.setLikeCount(0);
        marketDeck.setFavoriteCount(0);
        marketDeck.setDownloadCount(0);
        marketDeck.setRatingCount(0);
        marketDeck.setStatus(MarketDeckStatusEnum.PUBLISHED.getCode());
        marketDeck.setSortOrder(0);

        this.save(marketDeck);

        return convertToDTO(marketDeck, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long downloadDeck(Long marketDeckId, Long userId) {
        MarketDeck marketDeck = getById(marketDeckId);
        AssertUtil.notNull(marketDeck, ResponseEnum.DATA_NOT_EXISTS);
        AssertUtil.isTrue(Objects.equals(marketDeck.getStatus(), MarketDeckStatusEnum.PUBLISHED.getCode()),
                ResponseEnum.DATA_NOT_EXISTS);

        LambdaQueryWrapper<MarketDeckDownload> downloadWrapper = new LambdaQueryWrapper<>();
        downloadWrapper.eq(MarketDeckDownload::getMarketDeckId, marketDeckId)
                .eq(MarketDeckDownload::getUserId, userId);
        MarketDeckDownload existDownload = marketDeckDownloadDao.selectOne(downloadWrapper);

        if (existDownload != null) {
            return null;
        }

        Deck originalDeck = deckDao.selectById(marketDeck.getOriginalDeckId());
        AssertUtil.notNull(originalDeck, ResponseEnum.DATA_NOT_EXISTS);

        Deck newDeck = new Deck();
        newDeck.setUserId(userId);
        newDeck.setName(marketDeck.getName());
        newDeck.setDescription(marketDeck.getDescription());
        newDeck.setCoverImageUrl(marketDeck.getCoverImageUrl());
        newDeck.setCardCount(0);
        newDeck.setTodayReviewCount(0);
        newDeck.setMasteryRate(BigDecimal.ZERO);
        newDeck.setIsPublic(0);
        newDeck.setSortOrder(getNextDeckSortOrder(userId));
        deckDao.insert(newDeck);

        LambdaQueryWrapper<Card> cardWrapper = new LambdaQueryWrapper<>();
        cardWrapper.eq(Card::getDeckId, marketDeck.getOriginalDeckId());
        List<Card> originalCards = cardDao.selectList(cardWrapper);

        if (!CollectionUtils.isEmpty(originalCards)) {
            Map<Long, Long> cardIdMapping = new HashMap<>();

            for (Card originalCard : originalCards) {
                Card newCard = new Card();
                BeanUtils.copyProperties(originalCard, newCard);
                newCard.setId(null);
                newCard.setUserId(userId);
                newCard.setDeckId(newDeck.getId());
                cardDao.insert(newCard);
                cardIdMapping.put(originalCard.getId(), newCard.getId());
            }

            Set<Long> originalCardIds = originalCards.stream()
                    .map(Card::getId)
                    .collect(Collectors.toSet());

            LambdaQueryWrapper<CardTag> cardTagWrapper = new LambdaQueryWrapper<>();
            cardTagWrapper.in(CardTag::getCardId, originalCardIds);
            List<CardTag> originalCardTags = cardTagDao.selectList(cardTagWrapper);

            if (!CollectionUtils.isEmpty(originalCardTags)) {
                for (CardTag originalCardTag : originalCardTags) {
                    Long newCardId = cardIdMapping.get(originalCardTag.getCardId());
                    if (newCardId != null) {
                        CardTag newCardTag = new CardTag();
                        newCardTag.setCardId(newCardId);
                        newCardTag.setTagId(originalCardTag.getTagId());
                        cardTagDao.insert(newCardTag);
                    }
                }
            }

            int cardCount = originalCards.size();
            newDeck.setCardCount(cardCount);
            deckDao.updateById(newDeck);
        }

        MarketDeckDownload download = new MarketDeckDownload();
        download.setMarketDeckId(marketDeckId);
        download.setUserId(userId);
        download.setDownloadTime(System.currentTimeMillis());
        marketDeckDownloadDao.insert(download);

        marketDeck.setDownloadCount((marketDeck.getDownloadCount() != null ? marketDeck.getDownloadCount() : 0) + 1);
        this.updateById(marketDeck);

        return newDeck.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleLike(Long marketDeckId, Long userId) {
        MarketDeck marketDeck = getById(marketDeckId);
        AssertUtil.notNull(marketDeck, ResponseEnum.DATA_NOT_EXISTS);

        LambdaQueryWrapper<MarketDeckLike> likeWrapper = new LambdaQueryWrapper<>();
        likeWrapper.eq(MarketDeckLike::getMarketDeckId, marketDeckId)
                .eq(MarketDeckLike::getUserId, userId);
        MarketDeckLike existLike = marketDeckLikeDao.selectOne(likeWrapper);

        if (existLike != null) {
            marketDeckLikeDao.deleteById(existLike.getId());
            int newCount = Math.max(0, (marketDeck.getLikeCount() != null ? marketDeck.getLikeCount() : 0) - 1);
            marketDeck.setLikeCount(newCount);
            this.updateById(marketDeck);
            return false;
        } else {
            MarketDeckLike like = new MarketDeckLike();
            like.setMarketDeckId(marketDeckId);
            like.setUserId(userId);
            marketDeckLikeDao.insert(like);

            int newCount = (marketDeck.getLikeCount() != null ? marketDeck.getLikeCount() : 0) + 1;
            marketDeck.setLikeCount(newCount);
            this.updateById(marketDeck);
            return true;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleFavorite(Long marketDeckId, Long userId) {
        MarketDeck marketDeck = getById(marketDeckId);
        AssertUtil.notNull(marketDeck, ResponseEnum.DATA_NOT_EXISTS);

        LambdaQueryWrapper<MarketDeckFavorite> favoriteWrapper = new LambdaQueryWrapper<>();
        favoriteWrapper.eq(MarketDeckFavorite::getMarketDeckId, marketDeckId)
                .eq(MarketDeckFavorite::getUserId, userId);
        MarketDeckFavorite existFavorite = marketDeckFavoriteDao.selectOne(favoriteWrapper);

        if (existFavorite != null) {
            marketDeckFavoriteDao.deleteById(existFavorite.getId());
            int newCount = Math.max(0, (marketDeck.getFavoriteCount() != null ? marketDeck.getFavoriteCount() : 0) - 1);
            marketDeck.setFavoriteCount(newCount);
            this.updateById(marketDeck);
            return false;
        } else {
            MarketDeckFavorite favorite = new MarketDeckFavorite();
            favorite.setMarketDeckId(marketDeckId);
            favorite.setUserId(userId);
            marketDeckFavoriteDao.insert(favorite);

            int newCount = (marketDeck.getFavoriteCount() != null ? marketDeck.getFavoriteCount() : 0) + 1;
            marketDeck.setFavoriteCount(newCount);
            this.updateById(marketDeck);
            return true;
        }
    }

    @Override
    public MarketDeckDTO getMarketDeckById(Long marketDeckId, Long userId) {
        MarketDeck marketDeck = getById(marketDeckId);
        AssertUtil.notNull(marketDeck, ResponseEnum.DATA_NOT_EXISTS);
        return convertToDTO(marketDeck, userId);
    }

    @Override
    public PageResult<MarketDeckDTO> pageQueryMarketDecks(MarketDeckPageQuery query, Long userId) {
        LambdaQueryWrapper<MarketDeck> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MarketDeck::getStatus, MarketDeckStatusEnum.PUBLISHED.getCode());

        if (StringUtils.hasText(query.getKeyword())) {
            queryWrapper.and(w -> w
                    .like(MarketDeck::getName, query.getKeyword())
                    .or()
                    .like(MarketDeck::getDescription, query.getKeyword()));
        }

        if (!CollectionUtils.isEmpty(query.getTags())) {
            for (String tag : query.getTags()) {
                queryWrapper.like(MarketDeck::getTags, tag);
            }
        }

        if (query.getUserId() != null) {
            queryWrapper.eq(MarketDeck::getUserId, query.getUserId());
        }

        String sortBy = StringUtils.hasText(query.getSortBy()) ? query.getSortBy() : "downloadCount";
        String sortOrder = "desc".equalsIgnoreCase(query.getSortOrder()) ? "desc" : "asc";

        if ("downloadCount".equals(sortBy)) {
            if ("desc".equals(sortOrder)) {
                queryWrapper.orderByDesc(MarketDeck::getDownloadCount);
            } else {
                queryWrapper.orderByAsc(MarketDeck::getDownloadCount);
            }
        } else if ("likeCount".equals(sortBy)) {
            if ("desc".equals(sortOrder)) {
                queryWrapper.orderByDesc(MarketDeck::getLikeCount);
            } else {
                queryWrapper.orderByAsc(MarketDeck::getLikeCount);
            }
        } else {
            if ("desc".equals(sortOrder)) {
                queryWrapper.orderByDesc(MarketDeck::getCreateTime);
            } else {
                queryWrapper.orderByAsc(MarketDeck::getCreateTime);
            }
        }

        Page<MarketDeck> deckPage = this.page(
                new Page<>(query.getPageNo(), query.getPageSize()),
                queryWrapper
        );

        List<MarketDeckDTO> deckItems = convertToDTOList(deckPage.getRecords(), userId);

        return PageResult.of(deckItems, deckPage.getTotal(), query.getPageNo(), query.getPageSize());
    }

    @Override
    public List<MarketDeckDTO> getMyFavorites(Long userId) {
        LambdaQueryWrapper<MarketDeckFavorite> favoriteWrapper = new LambdaQueryWrapper<>();
        favoriteWrapper.eq(MarketDeckFavorite::getUserId, userId)
                .orderByDesc(MarketDeckFavorite::getCreateTime);
        List<MarketDeckFavorite> favorites = marketDeckFavoriteDao.selectList(favoriteWrapper);

        if (CollectionUtils.isEmpty(favorites)) {
            return new ArrayList<>();
        }

        List<Long> marketDeckIds = favorites.stream()
                .map(MarketDeckFavorite::getMarketDeckId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (marketDeckIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<MarketDeck> marketDecks = this.listByIds(marketDeckIds);

        Map<Long, MarketDeck> deckMap = marketDecks.stream()
                .collect(Collectors.toMap(MarketDeck::getId, d -> d));

        List<MarketDeck> sortedDecks = new ArrayList<>();
        for (Long id : marketDeckIds) {
            MarketDeck deck = deckMap.get(id);
            if (deck != null && Objects.equals(deck.getStatus(), MarketDeckStatusEnum.PUBLISHED.getCode())) {
                sortedDecks.add(deck);
            }
        }

        return convertToDTOList(sortedDecks, userId);
    }

    @Override
    public List<MarketDeckDTO> getMyShares(Long userId) {
        LambdaQueryWrapper<MarketDeck> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MarketDeck::getUserId, userId)
                .orderByDesc(MarketDeck::getCreateTime);

        List<MarketDeck> marketDecks = this.list(queryWrapper);
        return convertToDTOList(marketDecks, userId);
    }

    @Override
    public List<String> getHotTags(Integer limit) {
        int actualLimit = (limit != null && limit > 0) ? limit : 10;

        LambdaQueryWrapper<MarketDeck> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MarketDeck::getStatus, MarketDeckStatusEnum.PUBLISHED.getCode())
                .isNotNull(MarketDeck::getTags)
                .orderByDesc(MarketDeck::getDownloadCount)
                .last("LIMIT 100");

        List<MarketDeck> decks = this.list(queryWrapper);

        Map<String, Integer> tagCountMap = new HashMap<>();
        for (MarketDeck deck : decks) {
            if (StringUtils.hasText(deck.getTags())) {
                try {
                    List<String> tags = JSON.parseArray(deck.getTags(), String.class);
                    if (!CollectionUtils.isEmpty(tags)) {
                        for (String tag : tags) {
                            if (StringUtils.hasText(tag)) {
                                tagCountMap.merge(tag.trim(), 1, Integer::sum);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.warn("Failed to parse tags: {}", deck.getTags());
                }
            }
        }

        return tagCountMap.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(actualLimit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public MarketDeckDTO convertToDTO(MarketDeck marketDeck, Long userId) {
        if (marketDeck == null) {
            return null;
        }

        MarketDeckDTO dto = new MarketDeckDTO();
        BeanUtils.copyProperties(marketDeck, dto);

        if (StringUtils.hasText(marketDeck.getTags())) {
            try {
                dto.setTags(JSON.parseArray(marketDeck.getTags(), String.class));
            } catch (Exception e) {
                log.warn("Failed to parse tags: {}", marketDeck.getTags());
            }
        }

        if (marketDeck.getStatus() != null) {
            dto.setStatusDesc(MarketDeckStatusEnum.getDescByCode(marketDeck.getStatus()));
        }

        if (userId != null) {
            LambdaQueryWrapper<MarketDeckLike> likeWrapper = new LambdaQueryWrapper<>();
            likeWrapper.eq(MarketDeckLike::getMarketDeckId, marketDeck.getId())
                    .eq(MarketDeckLike::getUserId, userId);
            dto.setIsLiked(marketDeckLikeDao.selectCount(likeWrapper) > 0);

            LambdaQueryWrapper<MarketDeckFavorite> favoriteWrapper = new LambdaQueryWrapper<>();
            favoriteWrapper.eq(MarketDeckFavorite::getMarketDeckId, marketDeck.getId())
                    .eq(MarketDeckFavorite::getUserId, userId);
            dto.setIsFavorited(marketDeckFavoriteDao.selectCount(favoriteWrapper) > 0);

            LambdaQueryWrapper<MarketDeckDownload> downloadWrapper = new LambdaQueryWrapper<>();
            downloadWrapper.eq(MarketDeckDownload::getMarketDeckId, marketDeck.getId())
                    .eq(MarketDeckDownload::getUserId, userId);
            dto.setIsDownloaded(marketDeckDownloadDao.selectCount(downloadWrapper) > 0);
        }

        return dto;
    }

    @Override
    public List<MarketDeckDTO> convertToDTOList(List<MarketDeck> marketDecks, Long userId) {
        if (CollectionUtils.isEmpty(marketDecks)) {
            return new ArrayList<>();
        }

        Set<Long> deckIds = marketDecks.stream()
                .map(MarketDeck::getId)
                .collect(Collectors.toSet());

        Set<Long> likedDeckIds = new HashSet<>();
        Set<Long> favoritedDeckIds = new HashSet<>();
        Set<Long> downloadedDeckIds = new HashSet<>();

        if (userId != null && !deckIds.isEmpty()) {
            LambdaQueryWrapper<MarketDeckLike> likeWrapper = new LambdaQueryWrapper<>();
            likeWrapper.in(MarketDeckLike::getMarketDeckId, deckIds)
                    .eq(MarketDeckLike::getUserId, userId);
            List<MarketDeckLike> likes = marketDeckLikeDao.selectList(likeWrapper);
            for (MarketDeckLike like : likes) {
                likedDeckIds.add(like.getMarketDeckId());
            }

            LambdaQueryWrapper<MarketDeckFavorite> favoriteWrapper = new LambdaQueryWrapper<>();
            favoriteWrapper.in(MarketDeckFavorite::getMarketDeckId, deckIds)
                    .eq(MarketDeckFavorite::getUserId, userId);
            List<MarketDeckFavorite> favorites = marketDeckFavoriteDao.selectList(favoriteWrapper);
            for (MarketDeckFavorite favorite : favorites) {
                favoritedDeckIds.add(favorite.getMarketDeckId());
            }

            LambdaQueryWrapper<MarketDeckDownload> downloadWrapper = new LambdaQueryWrapper<>();
            downloadWrapper.in(MarketDeckDownload::getMarketDeckId, deckIds)
                    .eq(MarketDeckDownload::getUserId, userId);
            List<MarketDeckDownload> downloads = marketDeckDownloadDao.selectList(downloadWrapper);
            for (MarketDeckDownload download : downloads) {
                downloadedDeckIds.add(download.getMarketDeckId());
            }
        }

        List<MarketDeckDTO> result = new ArrayList<>();
        for (MarketDeck marketDeck : marketDecks) {
            MarketDeckDTO dto = new MarketDeckDTO();
            BeanUtils.copyProperties(marketDeck, dto);

            if (StringUtils.hasText(marketDeck.getTags())) {
                try {
                    dto.setTags(JSON.parseArray(marketDeck.getTags(), String.class));
                } catch (Exception e) {
                    log.warn("Failed to parse tags: {}", marketDeck.getTags());
                }
            }

            if (marketDeck.getStatus() != null) {
                dto.setStatusDesc(MarketDeckStatusEnum.getDescByCode(marketDeck.getStatus()));
            }

            dto.setIsLiked(likedDeckIds.contains(marketDeck.getId()));
            dto.setIsFavorited(favoritedDeckIds.contains(marketDeck.getId()));
            dto.setIsDownloaded(downloadedDeckIds.contains(marketDeck.getId()));

            result.add(dto);
        }

        return result;
    }

    private List<String> getDeckTags(Long deckId) {
        LambdaQueryWrapper<Card> cardWrapper = new LambdaQueryWrapper<>();
        cardWrapper.eq(Card::getDeckId, deckId);
        List<Card> cards = cardDao.selectList(cardWrapper);

        if (CollectionUtils.isEmpty(cards)) {
            return new ArrayList<>();
        }

        List<Long> cardIds = cards.stream()
                .map(Card::getId)
                .collect(Collectors.toList());

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

        List<Tag> tags = tagDao.selectBatchIds(tagIds);
        return tags.stream()
                .map(Tag::getName)
                .filter(StringUtils::hasText)
                .collect(Collectors.toList());
    }

    private Integer getNextDeckSortOrder(Long userId) {
        LambdaQueryWrapper<Deck> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Deck::getUserId, userId);
        queryWrapper.orderByDesc(Deck::getSortOrder);
        queryWrapper.last("LIMIT 1");

        Deck lastDeck = deckDao.selectOne(queryWrapper);
        if (lastDeck != null && lastDeck.getSortOrder() != null) {
            return lastDeck.getSortOrder() + 1;
        }
        return 1;
    }
}

package com.slack.slackjarservice.taskdashboard.controller;

import com.slack.slackjarservice.common.base.BaseController;
import com.slack.slackjarservice.common.enumtype.foundation.OperationEnum;
import com.slack.slackjarservice.common.response.ApiResponse;
import com.slack.slackjarservice.common.response.PageResult;
import com.slack.slackjarservice.taskdashboard.model.dto.spacedrepetition.*;
import com.slack.slackjarservice.taskdashboard.model.request.spacedrepetition.*;
import com.slack.slackjarservice.taskdashboard.service.spacedrepetition.*;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 间隔重复（知识卡片记忆）控制器
 *
 * @author system
 */
@RestController
@RequestMapping("/spaced-repetition")
public class SpacedRepetitionController extends BaseController {

    @Resource
    private DeckService deckService;

    @Resource
    private CardService cardService;

    @Resource
    private TagService tagService;

    @Resource
    private StudyStatService studyStatService;

    @Resource
    private MarketDeckService marketDeckService;

    // ==================== 卡组管理 ====================

    /**
     * 创建卡组
     */
    @PostMapping("/deck")
    public ApiResponse<DeckDTO> createDeck(@Validated @RequestBody CreateDeckRequest request) {
        Long userId = getLoginUserId();
        DeckDTO deck = deckService.createDeck(request, userId);
        recordOperateLog(OperationEnum.USER_UPSERT, "创建卡组:" + deck.getName());
        return success(deck);
    }

    /**
     * 更新卡组
     */
    @PutMapping("/deck/{deckId}")
    public ApiResponse<DeckDTO> updateDeck(
            @PathVariable Long deckId,
            @Validated @RequestBody UpdateDeckRequest request
    ) {
        Long userId = getLoginUserId();
        DeckDTO deck = deckService.updateDeck(deckId, request, userId);
        recordOperateLog(OperationEnum.USER_UPSERT, "更新卡组:" + deckId);
        return success(deck);
    }

    /**
     * 删除卡组
     */
    @DeleteMapping("/deck/{deckId}")
    public ApiResponse<Boolean> deleteDeck(@PathVariable Long deckId) {
        Long userId = getLoginUserId();
        deckService.deleteDeck(deckId, userId);
        recordOperateLog(OperationEnum.USER_UPSERT, "删除卡组:" + deckId);
        return success(true);
    }

    /**
     * 获取卡组详情
     */
    @GetMapping("/deck/{deckId}")
    public ApiResponse<DeckDTO> getDeckById(@PathVariable Long deckId) {
        Long userId = getLoginUserId();
        DeckDTO deck = deckService.getDeckById(deckId, userId);
        recordOperateLog(OperationEnum.USER_QUERY, "查询卡组详情:" + deckId);
        return success(deck);
    }

    /**
     * 分页查询卡组
     */
    @PostMapping("/deck/page-query")
    public ApiResponse<PageResult<DeckDTO>> pageQueryDecks(@RequestBody DeckPageQuery query) {
        Long userId = getLoginUserId();
        PageResult<DeckDTO> result = deckService.pageQueryDecks(query, userId);
        recordOperateLog(OperationEnum.USER_QUERY, "分页查询卡组列表");
        return success(result);
    }

    /**
     * 获取用户的所有卡组
     */
    @GetMapping("/deck/my")
    public ApiResponse<List<DeckDTO>> getMyDecks() {
        Long userId = getLoginUserId();
        List<DeckDTO> decks = deckService.getUserDecks(userId);
        return success(decks);
    }

    // ==================== 卡片管理 ====================

    /**
     * 创建卡片
     */
    @PostMapping("/card")
    public ApiResponse<CardDTO> createCard(@Validated @RequestBody CreateCardRequest request) {
        Long userId = getLoginUserId();
        CardDTO card = cardService.createCard(request, userId);
        recordOperateLog(OperationEnum.USER_UPSERT, "创建卡片");
        return success(card);
    }

    /**
     * 更新卡片
     */
    @PutMapping("/card/{cardId}")
    public ApiResponse<CardDTO> updateCard(
            @PathVariable Long cardId,
            @Validated @RequestBody UpdateCardRequest request
    ) {
        Long userId = getLoginUserId();
        CardDTO card = cardService.updateCard(cardId, request, userId);
        recordOperateLog(OperationEnum.USER_UPSERT, "更新卡片:" + cardId);
        return success(card);
    }

    /**
     * 删除卡片
     */
    @DeleteMapping("/card/{cardId}")
    public ApiResponse<Boolean> deleteCard(@PathVariable Long cardId) {
        Long userId = getLoginUserId();
        cardService.deleteCard(cardId, userId);
        recordOperateLog(OperationEnum.USER_UPSERT, "删除卡片:" + cardId);
        return success(true);
    }

    /**
     * 获取卡片详情
     */
    @GetMapping("/card/{cardId}")
    public ApiResponse<CardDTO> getCardById(@PathVariable Long cardId) {
        Long userId = getLoginUserId();
        CardDTO card = cardService.getCardById(cardId, userId);
        recordOperateLog(OperationEnum.USER_QUERY, "查询卡片详情:" + cardId);
        return success(card);
    }

    /**
     * 分页查询卡片
     */
    @PostMapping("/card/page-query")
    public ApiResponse<PageResult<CardDTO>> pageQueryCards(@RequestBody CardPageQuery query) {
        Long userId = getLoginUserId();
        PageResult<CardDTO> result = cardService.pageQueryCards(query, userId);
        recordOperateLog(OperationEnum.USER_QUERY, "分页查询卡片列表");
        return success(result);
    }

    /**
     * 获取今日需复习的卡片
     */
    @GetMapping("/card/today-review")
    public ApiResponse<List<CardDTO>> getTodayReviewCards(
            @RequestParam(required = false) Long deckId,
            @RequestParam(required = false, defaultValue = "20") Integer limit
    ) {
        Long userId = getLoginUserId();
        List<CardDTO> cards = cardService.getTodayReviewCards(userId, deckId, limit);
        return success(cards);
    }

    /**
     * 获取新卡片（未复习过的）
     */
    @GetMapping("/card/new")
    public ApiResponse<List<CardDTO>> getNewCards(
            @RequestParam(required = false) Long deckId,
            @RequestParam(required = false, defaultValue = "20") Integer limit
    ) {
        Long userId = getLoginUserId();
        List<CardDTO> cards = cardService.getNewCards(userId, deckId, limit);
        return success(cards);
    }

    // ==================== 复习功能 ====================

    /**
     * 复习卡片
     */
    @PostMapping("/review")
    public ApiResponse<ReviewResultDTO> reviewCard(@Valid @RequestBody ReviewCardRequest request) {
        Long userId = getLoginUserId();
        ReviewResultDTO result = cardService.reviewCard(request, userId);
        recordOperateLog(OperationEnum.USER_UPSERT, "复习卡片:" + request.getCardId());
        return success(result);
    }

    // ==================== 标签管理 ====================

    /**
     * 创建标签
     */
    @PostMapping("/tag")
    public ApiResponse<TagDTO> createTag(@Validated @RequestBody CreateTagRequest request) {
        Long userId = getLoginUserId();
        TagDTO tag = tagService.createTag(request, userId);
        recordOperateLog(OperationEnum.USER_UPSERT, "创建标签:" + tag.getName());
        return success(tag);
    }

    /**
     * 更新标签
     */
    @PutMapping("/tag/{tagId}")
    public ApiResponse<TagDTO> updateTag(
            @PathVariable Long tagId,
            @Validated @RequestBody CreateTagRequest request
    ) {
        Long userId = getLoginUserId();
        TagDTO tag = tagService.updateTag(tagId, request, userId);
        recordOperateLog(OperationEnum.USER_UPSERT, "更新标签:" + tagId);
        return success(tag);
    }

    /**
     * 删除标签
     */
    @DeleteMapping("/tag/{tagId}")
    public ApiResponse<Boolean> deleteTag(@PathVariable Long tagId) {
        Long userId = getLoginUserId();
        tagService.deleteTag(tagId, userId);
        recordOperateLog(OperationEnum.USER_UPSERT, "删除标签:" + tagId);
        return success(true);
    }

    /**
     * 获取用户的所有标签
     */
    @GetMapping("/tag/my")
    public ApiResponse<List<TagDTO>> getMyTags() {
        Long userId = getLoginUserId();
        List<TagDTO> tags = tagService.getUserTags(userId);
        return success(tags);
    }

    // ==================== 学习统计 ====================

    /**
     * 获取学习仪表盘数据
     */
    @GetMapping("/dashboard")
    public ApiResponse<StudyDashboardDTO> getStudyDashboard() {
        Long userId = getLoginUserId();
        StudyDashboardDTO dashboard = studyStatService.getStudyDashboard(userId);
        return success(dashboard);
    }

    /**
     * 获取今日学习统计
     */
    @GetMapping("/stat/today")
    public ApiResponse<StudyStatDTO> getTodayStat() {
        Long userId = getLoginUserId();
        StudyStatDTO stat = studyStatService.getTodayStat(userId);
        return success(stat);
    }

    /**
     * 获取近N天的学习统计
     */
    @GetMapping("/stat/recent")
    public ApiResponse<List<StudyStatDTO>> getRecentStats(
            @RequestParam(required = false, defaultValue = "7") Integer days
    ) {
        Long userId = getLoginUserId();
        List<StudyStatDTO> stats = studyStatService.getRecentStats(userId, days);
        return success(stats);
    }

    // ==================== 卡片市场 ====================

    /**
     * 分享卡组到市场
     */
    @PostMapping("/market/share")
    public ApiResponse<MarketDeckDTO> shareToMarket(@Validated @RequestBody ShareToMarketRequest request) {
        Long userId = getLoginUserId();
        MarketDeckDTO marketDeck = marketDeckService.shareToMarket(request, userId);
        recordOperateLog(OperationEnum.USER_UPSERT, "分享卡组到市场:" + request.getDeckId());
        return success(marketDeck);
    }

    /**
     * 从市场下载卡组
     */
    @PostMapping("/market/download/{marketDeckId}")
    public ApiResponse<Long> downloadDeck(@PathVariable Long marketDeckId) {
        Long userId = getLoginUserId();
        Long deckId = marketDeckService.downloadDeck(marketDeckId, userId);
        recordOperateLog(OperationEnum.USER_UPSERT, "下载市场卡组:" + marketDeckId);
        return success(deckId);
    }

    /**
     * 点赞/取消点赞
     */
    @PostMapping("/market/like/{marketDeckId}")
    public ApiResponse<Boolean> toggleLike(@PathVariable Long marketDeckId) {
        Long userId = getLoginUserId();
        boolean isLiked = marketDeckService.toggleLike(marketDeckId, userId);
        return success(isLiked);
    }

    /**
     * 收藏/取消收藏
     */
    @PostMapping("/market/favorite/{marketDeckId}")
    public ApiResponse<Boolean> toggleFavorite(@PathVariable Long marketDeckId) {
        Long userId = getLoginUserId();
        boolean isFavorited = marketDeckService.toggleFavorite(marketDeckId, userId);
        return success(isFavorited);
    }

    /**
     * 获取市场卡组详情
     */
    @GetMapping("/market/{marketDeckId}")
    public ApiResponse<MarketDeckDTO> getMarketDeckById(@PathVariable Long marketDeckId) {
        Long userId = getLoginUserId();
        MarketDeckDTO deck = marketDeckService.getMarketDeckById(marketDeckId, userId);
        recordOperateLog(OperationEnum.USER_QUERY, "查询市场卡组详情:" + marketDeckId);
        return success(deck);
    }

    /**
     * 分页查询市场卡组
     */
    @PostMapping("/market/page-query")
    public ApiResponse<PageResult<MarketDeckDTO>> pageQueryMarketDecks(@RequestBody MarketDeckPageQuery query) {
        Long userId = getLoginUserId();
        PageResult<MarketDeckDTO> result = marketDeckService.pageQueryMarketDecks(query, userId);
        recordOperateLog(OperationEnum.USER_QUERY, "分页查询市场卡组列表");
        return success(result);
    }

    /**
     * 获取我的收藏
     */
    @GetMapping("/market/my-favorites")
    public ApiResponse<List<MarketDeckDTO>> getMyFavorites() {
        Long userId = getLoginUserId();
        List<MarketDeckDTO> decks = marketDeckService.getMyFavorites(userId);
        return success(decks);
    }

    /**
     * 获取我的分享
     */
    @GetMapping("/market/my-shares")
    public ApiResponse<List<MarketDeckDTO>> getMyShares() {
        Long userId = getLoginUserId();
        List<MarketDeckDTO> decks = marketDeckService.getMyShares(userId);
        return success(decks);
    }

    /**
     * 获取热门标签
     */
    @GetMapping("/market/hot-tags")
    public ApiResponse<List<String>> getHotTags(
            @RequestParam(required = false, defaultValue = "10") Integer limit
    ) {
        List<String> tags = marketDeckService.getHotTags(limit);
        return success(tags);
    }
}

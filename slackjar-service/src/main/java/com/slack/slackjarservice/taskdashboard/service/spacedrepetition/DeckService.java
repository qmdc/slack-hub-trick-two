package com.slack.slackjarservice.taskdashboard.service.spacedrepetition;

import com.baomidou.mybatisplus.extension.service.IService;
import com.slack.slackjarservice.common.response.PageResult;
import com.slack.slackjarservice.taskdashboard.entity.spacedrepetition.Deck;
import com.slack.slackjarservice.taskdashboard.model.dto.spacedrepetition.DeckDTO;
import com.slack.slackjarservice.taskdashboard.model.request.spacedrepetition.CreateDeckRequest;
import com.slack.slackjarservice.taskdashboard.model.request.spacedrepetition.DeckPageQuery;
import com.slack.slackjarservice.taskdashboard.model.request.spacedrepetition.UpdateDeckRequest;

import java.util.List;

/**
 * 闪卡组服务接口
 *
 * @author system
 */
public interface DeckService extends IService<Deck> {

    /**
     * 创建卡组
     *
     * @param request 创建卡组请求
     * @param userId 用户ID
     * @return 卡组DTO
     */
    DeckDTO createDeck(CreateDeckRequest request, Long userId);

    /**
     * 更新卡组
     *
     * @param deckId 卡组ID
     * @param request 更新卡组请求
     * @param userId 用户ID
     * @return 卡组DTO
     */
    DeckDTO updateDeck(Long deckId, UpdateDeckRequest request, Long userId);

    /**
     * 删除卡组
     *
     * @param deckId 卡组ID
     * @param userId 用户ID
     */
    void deleteDeck(Long deckId, Long userId);

    /**
     * 获取卡组详情
     *
     * @param deckId 卡组ID
     * @param userId 用户ID
     * @return 卡组DTO
     */
    DeckDTO getDeckById(Long deckId, Long userId);

    /**
     * 分页查询卡组
     *
     * @param query 查询条件
     * @param userId 用户ID
     * @return 分页结果
     */
    PageResult<DeckDTO> pageQueryDecks(DeckPageQuery query, Long userId);

    /**
     * 获取用户的卡组列表
     *
     * @param userId 用户ID
     * @return 卡组列表
     */
    List<DeckDTO> getUserDecks(Long userId);

    /**
     * 更新今日待复习数量
     *
     * @param deckId 卡组ID
     */
    void updateTodayReviewCount(Long deckId);

    /**
     * 更新卡组掌握率
     *
     * @param deckId 卡组ID
     */
    void updateMasteryRate(Long deckId);

    /**
     * 转换实体为DTO
     *
     * @param deck 卡组实体
     * @return 卡组DTO
     */
    DeckDTO convertToDTO(Deck deck);

    /**
     * 批量转换实体为DTO
     *
     * @param decks 卡组实体列表
     * @return 卡组DTO列表
     */
    List<DeckDTO> convertToDTOList(List<Deck> decks);
}

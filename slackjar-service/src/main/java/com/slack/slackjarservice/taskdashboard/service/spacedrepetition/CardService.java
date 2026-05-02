package com.slack.slackjarservice.taskdashboard.service.spacedrepetition;

import com.baomidou.mybatisplus.extension.service.IService;
import com.slack.slackjarservice.common.response.PageResult;
import com.slack.slackjarservice.taskdashboard.entity.spacedrepetition.Card;
import com.slack.slackjarservice.taskdashboard.model.dto.spacedrepetition.CardDTO;
import com.slack.slackjarservice.taskdashboard.model.dto.spacedrepetition.ReviewResultDTO;
import com.slack.slackjarservice.taskdashboard.model.request.spacedrepetition.CardPageQuery;
import com.slack.slackjarservice.taskdashboard.model.request.spacedrepetition.CreateCardRequest;
import com.slack.slackjarservice.taskdashboard.model.request.spacedrepetition.ReviewCardRequest;
import com.slack.slackjarservice.taskdashboard.model.request.spacedrepetition.UpdateCardRequest;

import java.util.List;

/**
 * 闪卡服务接口
 *
 * @author system
 */
public interface CardService extends IService<Card> {

    /**
     * 创建卡片
     *
     * @param request 创建卡片请求
     * @param userId 用户ID
     * @return 卡片DTO
     */
    CardDTO createCard(CreateCardRequest request, Long userId);

    /**
     * 更新卡片
     *
     * @param cardId 卡片ID
     * @param request 更新卡片请求
     * @param userId 用户ID
     * @return 卡片DTO
     */
    CardDTO updateCard(Long cardId, UpdateCardRequest request, Long userId);

    /**
     * 删除卡片
     *
     * @param cardId 卡片ID
     * @param userId 用户ID
     */
    void deleteCard(Long cardId, Long userId);

    /**
     * 获取卡片详情
     *
     * @param cardId 卡片ID
     * @param userId 用户ID
     * @return 卡片DTO
     */
    CardDTO getCardById(Long cardId, Long userId);

    /**
     * 分页查询卡片
     *
     * @param query 查询条件
     * @param userId 用户ID
     * @return 分页结果
     */
    PageResult<CardDTO> pageQueryCards(CardPageQuery query, Long userId);

    /**
     * 获取今日需复习的卡片
     *
     * @param userId 用户ID
     * @param deckId 卡组ID（可为空）
     * @param limit 返回数量限制
     * @return 卡片列表
     */
    List<CardDTO> getTodayReviewCards(Long userId, Long deckId, Integer limit);

    /**
     * 获取新卡片（未复习过的）
     *
     * @param userId 用户ID
     * @param deckId 卡组ID（可为空）
     * @param limit 返回数量限制
     * @return 卡片列表
     */
    List<CardDTO> getNewCards(Long userId, Long deckId, Integer limit);

    /**
     * 复习卡片
     *
     * @param request 复习请求
     * @param userId 用户ID
     * @return 复习结果DTO
     */
    ReviewResultDTO reviewCard(ReviewCardRequest request, Long userId);

    /**
     * 获取卡组的卡片数量
     *
     * @param deckId 卡组ID
     * @return 卡片数量
     */
    int countCardsByDeckId(Long deckId);

    /**
     * 获取今日需复习的卡片数量
     *
     * @param userId 用户ID
     * @param deckId 卡组ID（可为空）
     * @return 数量
     */
    int countTodayReviewCards(Long userId, Long deckId);

    /**
     * 转换实体为DTO
     *
     * @param card 卡片实体
     * @return 卡片DTO
     */
    CardDTO convertToDTO(Card card);

    /**
     * 批量转换实体为DTO
     *
     * @param cards 卡片实体列表
     * @return 卡片DTO列表
     */
    List<CardDTO> convertToDTOList(List<Card> cards);
}

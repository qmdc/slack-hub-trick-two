package com.slack.slackjarservice.taskdashboard.service.spacedrepetition;

import com.baomidou.mybatisplus.extension.service.IService;
import com.slack.slackjarservice.common.response.PageResult;
import com.slack.slackjarservice.taskdashboard.entity.spacedrepetition.MarketDeck;
import com.slack.slackjarservice.taskdashboard.model.dto.spacedrepetition.MarketDeckDTO;
import com.slack.slackjarservice.taskdashboard.model.request.spacedrepetition.MarketDeckPageQuery;
import com.slack.slackjarservice.taskdashboard.model.request.spacedrepetition.ShareToMarketRequest;

import java.util.List;

/**
 * 市场卡组服务接口
 *
 * @author system
 */
public interface MarketDeckService extends IService<MarketDeck> {

    /**
     * 分享卡组到市场
     *
     * @param request 分享请求
     * @param userId 用户ID
     * @return 市场卡组DTO
     */
    MarketDeckDTO shareToMarket(ShareToMarketRequest request, Long userId);

    /**
     * 从市场下载卡组
     *
     * @param marketDeckId 市场卡组ID
     * @param userId 用户ID
     * @return 下载的卡组ID
     */
    Long downloadDeck(Long marketDeckId, Long userId);

    /**
     * 点赞/取消点赞
     *
     * @param marketDeckId 市场卡组ID
     * @param userId 用户ID
     * @return 是否点赞
     */
    boolean toggleLike(Long marketDeckId, Long userId);

    /**
     * 收藏/取消收藏
     *
     * @param marketDeckId 市场卡组ID
     * @param userId 用户ID
     * @return 是否收藏
     */
    boolean toggleFavorite(Long marketDeckId, Long userId);

    /**
     * 获取市场卡组详情
     *
     * @param marketDeckId 市场卡组ID
     * @param userId 用户ID
     * @return 市场卡组DTO
     */
    MarketDeckDTO getMarketDeckById(Long marketDeckId, Long userId);

    /**
     * 分页查询市场卡组
     *
     * @param query 查询条件
     * @param userId 用户ID
     * @return 分页结果
     */
    PageResult<MarketDeckDTO> pageQueryMarketDecks(MarketDeckPageQuery query, Long userId);

    /**
     * 获取我的收藏
     *
     * @param userId 用户ID
     * @return 卡组列表
     */
    List<MarketDeckDTO> getMyFavorites(Long userId);

    /**
     * 获取我的分享
     *
     * @param userId 用户ID
     * @return 卡组列表
     */
    List<MarketDeckDTO> getMyShares(Long userId);

    /**
     * 获取热门标签
     *
     * @param limit 返回数量限制
     * @return 标签列表
     */
    List<String> getHotTags(Integer limit);

    /**
     * 转换实体为DTO
     *
     * @param marketDeck 市场卡组实体
     * @param userId 用户ID（用于判断是否点赞/收藏）
     * @return 市场卡组DTO
     */
    MarketDeckDTO convertToDTO(MarketDeck marketDeck, Long userId);

    /**
     * 批量转换实体为DTO
     *
     * @param marketDecks 市场卡组实体列表
     * @param userId 用户ID
     * @return 市场卡组DTO列表
     */
    List<MarketDeckDTO> convertToDTOList(List<MarketDeck> marketDecks, Long userId);
}

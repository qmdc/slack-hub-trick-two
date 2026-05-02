package com.slack.slackjarservice.taskdashboard.service.spacedrepetition;

import com.baomidou.mybatisplus.extension.service.IService;
import com.slack.slackjarservice.taskdashboard.entity.spacedrepetition.Tag;
import com.slack.slackjarservice.taskdashboard.model.dto.spacedrepetition.TagDTO;
import com.slack.slackjarservice.taskdashboard.model.request.spacedrepetition.CreateTagRequest;

import java.util.List;

/**
 * 标签服务接口
 *
 * @author system
 */
public interface TagService extends IService<Tag> {

    /**
     * 创建标签
     *
     * @param request 创建标签请求
     * @param userId 用户ID
     * @return 标签DTO
     */
    TagDTO createTag(CreateTagRequest request, Long userId);

    /**
     * 更新标签
     *
     * @param tagId 标签ID
     * @param request 创建标签请求
     * @param userId 用户ID
     * @return 标签DTO
     */
    TagDTO updateTag(Long tagId, CreateTagRequest request, Long userId);

    /**
     * 删除标签
     *
     * @param tagId 标签ID
     * @param userId 用户ID
     */
    void deleteTag(Long tagId, Long userId);

    /**
     * 获取用户的所有标签
     *
     * @param userId 用户ID
     * @return 标签列表
     */
    List<TagDTO> getUserTags(Long userId);

    /**
     * 为卡片添加标签
     *
     * @param cardId 卡片ID
     * @param tagIds 标签ID列表
     * @param userId 用户ID
     */
    void addTagsToCard(Long cardId, List<Long> tagIds, Long userId);

    /**
     * 移除卡片的标签
     *
     * @param cardId 卡片ID
     * @param tagId 标签ID
     * @param userId 用户ID
     */
    void removeTagFromCard(Long cardId, Long tagId, Long userId);

    /**
     * 获取卡片的标签列表
     *
     * @param cardId 卡片ID
     * @return 标签列表
     */
    List<TagDTO> getCardTags(Long cardId);

    /**
     * 转换实体为DTO
     *
     * @param tag 标签实体
     * @return 标签DTO
     */
    TagDTO convertToDTO(Tag tag);

    /**
     * 批量转换实体为DTO
     *
     * @param tags 标签实体列表
     * @return 标签DTO列表
     */
    List<TagDTO> convertToDTOList(List<Tag> tags);
}

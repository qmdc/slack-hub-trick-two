package com.slack.slackjarservice.taskdashboard.service.spacedrepetition;

import com.baomidou.mybatisplus.extension.service.IService;
import com.slack.slackjarservice.taskdashboard.entity.spacedrepetition.StudyStat;
import com.slack.slackjarservice.taskdashboard.model.dto.spacedrepetition.StudyDashboardDTO;
import com.slack.slackjarservice.taskdashboard.model.dto.spacedrepetition.StudyStatDTO;

import java.util.List;

/**
 * 学习统计服务接口
 *
 * @author system
 */
public interface StudyStatService extends IService<StudyStat> {

    /**
     * 获取学习仪表盘数据
     *
     * @param userId 用户ID
     * @return 仪表盘数据
     */
    StudyDashboardDTO getStudyDashboard(Long userId);

    /**
     * 获取今日学习统计
     *
     * @param userId 用户ID
     * @return 今日统计数据
     */
    StudyStatDTO getTodayStat(Long userId);

    /**
     * 获取近N天的学习统计
     *
     * @param userId 用户ID
     * @param days 天数
     * @return 统计列表
     */
    List<StudyStatDTO> getRecentStats(Long userId, Integer days);

    /**
     * 记录学习时长
     *
     * @param userId 用户ID
     * @param duration 时长（毫秒）
     */
    void recordStudyDuration(Long userId, Long duration);

    /**
     * 记录复习结果
     *
     * @param userId 用户ID
     * @param isCorrect 是否正确
     * @param isNewCard 是否为新卡片
     */
    void recordReviewResult(Long userId, boolean isCorrect, boolean isNewCard);

    /**
     * 转换实体为DTO
     *
     * @param stat 统计实体
     * @return 统计DTO
     */
    StudyStatDTO convertToDTO(StudyStat stat);

    /**
     * 批量转换实体为DTO
     *
     * @param stats 统计实体列表
     * @return 统计DTO列表
     */
    List<StudyStatDTO> convertToDTOList(List<StudyStat> stats);
}

package com.slack.slackjarservice.taskdashboard.entity.spacedrepetition;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.slack.slackjarservice.common.base.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 卡片-标签关联实体类
 * 多对多关系表
 *
 * @author system
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sp_card_tag")
public class CardTag extends BaseModel {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 卡片ID
     */
    private Long cardId;

    /**
     * 标签ID
     */
    private Long tagId;
}

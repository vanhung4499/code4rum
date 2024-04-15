package com.hnv99.forum.service.notify.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hnv99.forum.api.model.entity.BaseDO;
import com.hnv99.forum.api.model.enums.NotifyTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Entity class for notification messages.
 */
@Data
@Accessors(chain = true)
@TableName("notify_msg")
public class NotifyMsgDO extends BaseDO {
    private static final long serialVersionUID = -4043774744889659100L;

    /**
     * The subject related to the message:
     * - For actions like article favorites, comments, replies to comments, and likes, this field stores the article ID.
     * - For system notification messages, it stores the primary key of the system notification message text, or it can be 0.
     * - For follows, it's also 0.
     */
    private Long relatedId;

    /**
     * The content of the message.
     */
    private String msg;

    /**
     * The user ID to whom the notification is sent.
     */
    private Long notifyUserId;

    /**
     * The user ID who triggered this message.
     */
    private Long operateUserId;

    /**
     * The type of message.
     *
     * @see NotifyTypeEnum#getType()
     */
    private Integer type;

    /**
     * 0 for unread, 1 for read.
     */
    private Integer state;
}


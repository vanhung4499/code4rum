package com.hnv99.forum.api.model.vo.notify.dto;

import lombok.Data;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Notification Message Data Transfer Object
 */
@Data
public class NotifyMsgDTO implements Serializable {
    private static final long serialVersionUID = 3833777672628522348L;

    private Long msgId;

    /**
     * The subject related to the message, such as article or comment
     */
    private String relatedId;

    /**
     * Related information
     */
    private String relatedInfo;

    /**
     * The user ID initiating the message
     */
    private Long operateUserId;

    /**
     * The username of the user initiating the message
     */
    private String operateUserName;

    /**
     * The profile photo URL of the user initiating the message
     */
    private String operateUserPhoto;

    /**
     * Message type
     */
    private Integer type;

    /**
     * Message content
     */
    private String msg;

    /**
     * 1 for read, 0 for unread
     */
    private Integer state;

    /**
     * Timestamp of message creation
     */
    private Timestamp createTime;
}

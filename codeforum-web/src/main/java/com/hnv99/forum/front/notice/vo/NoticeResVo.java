package com.hnv99.forum.front.notice.vo;

import com.hnv99.forum.api.model.vo.PageListVo;
import com.hnv99.forum.api.model.vo.notify.dto.NotifyMsgDTO;
import lombok.Data;
import java.util.Map;

/**
 * Response object for notifications.
 * Contains a list of notification messages, unread counts for each category, and the currently selected message type.
 */
@Data
public class NoticeResVo {

    /**
     * List of notification messages.
     */
    private PageListVo<NotifyMsgDTO> list;

    /**
     * Unread counts for each category.
     */
    private Map<String, Integer> unreadCountMap;

    /**
     * Currently selected message type.
     */
    private String selectType;
}


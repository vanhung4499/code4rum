package com.hnv99.forum.service.notify.service;

import com.hnv99.forum.api.model.enums.NotifyTypeEnum;
import com.hnv99.forum.api.model.vo.PageListVo;
import com.hnv99.forum.api.model.vo.PageParam;
import com.hnv99.forum.api.model.vo.notify.dto.NotifyMsgDTO;
import com.hnv99.forum.service.user.repository.entity.UserFootDO;

import java.util.Map;

/**
 * Service interface for notifications.
 */
public interface NotifyService {

    /**
     * Query the number of unread messages for a user.
     *
     * @param userId the ID of the user
     * @return the number of unread messages
     */
    int queryUserNotifyMsgCount(Long userId);

    /**
     * Query the list of notifications for a user.
     *
     * @param userId the ID of the user
     * @param type   the type of notification
     * @param page   the pagination parameters
     * @return a paginated list of notifications
     */
    PageListVo<NotifyMsgDTO> queryUserNotices(Long userId, NotifyTypeEnum type, PageParam page);

    /**
     * Query the number of unread messages.
     * @param userId the ID of the user
     * @return a map containing the counts of unread messages
     */
    Map<String, Integer> queryUnreadCounts(long userId);

    /**
     * Save a notification.
     *
     * @param foot the user foot
     * @param notifyTypeEnum the type of notification
     */
    void saveArticleNotify(UserFootDO foot, NotifyTypeEnum notifyTypeEnum);
}


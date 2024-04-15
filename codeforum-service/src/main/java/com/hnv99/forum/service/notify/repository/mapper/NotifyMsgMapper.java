package com.hnv99.forum.service.notify.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hnv99.forum.api.model.vo.PageParam;
import com.hnv99.forum.api.model.vo.notify.dto.NotifyMsgDTO;
import com.hnv99.forum.service.notify.repository.entity.NotifyMsgDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper interface for handling notification messages.
 */
public interface NotifyMsgMapper extends BaseMapper<NotifyMsgDO> {

    /**
     * Query the list of notifications related to articles.
     *
     * @param userId the user ID
     * @param type the type of notification
     * @param page the pagination information
     * @return the list of notification DTOs
     */
    List<NotifyMsgDTO> listArticleRelatedNotices(@Param("userId") long userId, @Param("type") int type, @Param("pageParam") PageParam page);

    /**
     * Query the list of notifications such as follows, system notifications, etc., which do not have related IDs.
     *
     * @param userId the user ID
     * @param type the type of notification
     * @param page the pagination information
     * @return the list of notification DTOs
     */
    List<NotifyMsgDTO> listNormalNotices(@Param("userId") long userId, @Param("type") int type, @Param("pageParam") PageParam page);

    /**
     * Mark notifications as read.
     *
     * @param ids the IDs of the notifications to be marked as read
     */
    void updateNoticeRead(@Param("ids") List<Long> ids);
}


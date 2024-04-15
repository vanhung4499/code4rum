package com.hnv99.forum.service.notify.repository.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnv99.forum.api.model.enums.NotifyStatEnum;
import com.hnv99.forum.api.model.enums.NotifyTypeEnum;
import com.hnv99.forum.api.model.vo.PageParam;
import com.hnv99.forum.api.model.vo.notify.dto.NotifyMsgDTO;
import com.hnv99.forum.service.notify.repository.entity.NotifyMsgDO;
import com.hnv99.forum.service.notify.repository.mapper.NotifyMsgMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Repository class for managing notification messages.
 */
@Repository
public class NotifyMsgDao extends ServiceImpl<NotifyMsgMapper, NotifyMsgDO> {

    /**
     * Query message records for idempotent filtering.
     *
     * @param msg the notification message to query
     * @return the notification message found, or null if not found
     */
    public NotifyMsgDO getByUserIdRelatedIdAndType(NotifyMsgDO msg) {
        List<NotifyMsgDO> list = lambdaQuery()
                .eq(NotifyMsgDO::getNotifyUserId, msg.getNotifyUserId())
                .eq(NotifyMsgDO::getOperateUserId, msg.getOperateUserId())
                .eq(NotifyMsgDO::getType, msg.getType())
                .eq(NotifyMsgDO::getRelatedId, msg.getRelatedId())
                .page(new Page<>(0, 1))
                .getRecords();
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }

    /**
     * Count the number of notification messages for a user with a specific status.
     *
     * @param userId the user ID
     * @param stat the status of the notification messages
     * @return the count of notification messages
     */
    public int countByUserIdAndStat(long userId, Integer stat) {
        return lambdaQuery()
                .eq(NotifyMsgDO::getNotifyUserId, userId)
                .eq(stat != null, NotifyMsgDO::getState, stat)
                .count().intValue();
    }

    /**
     * Group the count of unread notification messages by type for a user.
     *
     * @param userId the user ID
     * @param stat the status of the notification messages
     * @return a map containing the count of unread messages for each type
     */
    public Map<Integer, Integer> groupCountByUserIdAndStat(long userId, Integer stat) {
        QueryWrapper<NotifyMsgDO> wrapper = new QueryWrapper<>();
        wrapper.select("type, count(*) as cnt");
        wrapper.eq("notify_user_id", userId);
        if (stat != null) {
            wrapper.eq("state", stat);
        }
        wrapper.groupBy("type");
        List<Map<String, Object>> map = listMaps(wrapper);
        Map<Integer, Integer> result = new HashMap<>();
        map.forEach(s -> {
            result.put(Integer.valueOf(s.get("type").toString()), Integer.valueOf(s.get("cnt").toString()));
        });
        return result;
    }

    /**
     * Retrieve a list of notification messages for a user and a specific type.
     *
     * @param userId the user ID
     * @param type the type of notification messages
     * @param page the pagination information
     * @return the list of notification DTOs
     */
    public List<NotifyMsgDTO> listNotifyMsgByUserIdAndType(long userId, NotifyTypeEnum type, PageParam page) {
        switch (type) {
            case REPLY:
            case COMMENT:
            case COLLECT:
            case PRAISE:
                return baseMapper.listArticleRelatedNotices(userId, type.getType(), page);
            default:
                return baseMapper.listNormalNotices(userId, type.getType(), page);
        }
    }

    /**
     * Mark a list of notification messages as read.
     *
     * @param list the list of notification DTOs
     */
    public void updateNotifyMsgToRead(List<NotifyMsgDTO> list) {
        List<Long> ids = list.stream()
                .filter(s -> s.getState() == NotifyStatEnum.UNREAD.getStat())
                .map(NotifyMsgDTO::getMsgId)
                .collect(Collectors.toList());
        if (!ids.isEmpty()) {
            baseMapper.updateNoticeRead(ids);
        }
    }
}

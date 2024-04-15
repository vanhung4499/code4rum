package com.hnv99.forum.service.notify.service.impl;

import com.hnv99.forum.api.model.context.ReqInfoContext;
import com.hnv99.forum.api.model.enums.NotifyStatEnum;
import com.hnv99.forum.api.model.enums.NotifyTypeEnum;
import com.hnv99.forum.api.model.vo.PageListVo;
import com.hnv99.forum.api.model.vo.PageParam;
import com.hnv99.forum.api.model.vo.notify.dto.NotifyMsgDTO;
import com.hnv99.forum.core.util.NumUtil;
import com.hnv99.forum.service.notify.repository.dao.NotifyMsgDao;
import com.hnv99.forum.service.notify.repository.entity.NotifyMsgDO;
import com.hnv99.forum.service.notify.service.NotifyService;
import com.hnv99.forum.service.user.repository.entity.UserFootDO;
import com.hnv99.forum.service.user.service.UserRelationService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service implementation for managing notifications.
 */
@Service
public class NotifyServiceImpl implements NotifyService {

    @Resource
    private NotifyMsgDao notifyMsgDao;

    @Resource
    private UserRelationService userRelationService;

    @Override
    public int queryUserNotifyMsgCount(Long userId) {
        return notifyMsgDao.countByUserIdAndStat(userId, NotifyStatEnum.UNREAD.getStat());
    }

    /**
     * Query the notification message list.
     *
     * @return the page list of notification DTOs
     */
    @Override
    public PageListVo<NotifyMsgDTO> queryUserNotices(Long userId, NotifyTypeEnum type, PageParam page) {
        List<NotifyMsgDTO> list = notifyMsgDao.listNotifyMsgByUserIdAndType(userId, type, page);
        if (CollectionUtils.isEmpty(list)) {
            return PageListVo.emptyVo();
        }

        // Set messages as read
        notifyMsgDao.updateNotifyMsgToRead(list);
        // Update the global total message count
        ReqInfoContext.getReqInfo().setMsgNum(queryUserNotifyMsgCount(userId));
        // Update the follow status of the current logged-in user
        updateFollowStatus(userId, list);
        return PageListVo.newVo(list, page.getPageSize());
    }

    private void updateFollowStatus(Long userId, List<NotifyMsgDTO> list) {
        List<Long> targetUserIds = list.stream()
                .filter(s -> s.getType() == NotifyTypeEnum.FOLLOW.getType())
                .map(NotifyMsgDTO::getOperateUserId)
                .collect(Collectors.toList());
        if (targetUserIds.isEmpty()) {
            return;
        }

        // Query the list of users that userId has already followed; and set the corresponding msg to true, indicating that they have already been followed and don't need to follow again
        Set<Long> followedUserIds = userRelationService.getFollowedUserId(targetUserIds, userId);
        list.forEach(notify -> {
            if (followedUserIds.contains(notify.getOperateUserId())) {
                notify.setMsg("true");
            } else {
                notify.setMsg("false");
            }
        });
    }

    @Override
    public Map<String, Integer> queryUnreadCounts(long userId) {
        Map<Integer, Integer> map = Collections.emptyMap();
        if (ReqInfoContext.getReqInfo() != null && NumUtil.upZero(ReqInfoContext.getReqInfo().getMsgNum())) {
            map = notifyMsgDao.groupCountByUserIdAndStat(userId, NotifyStatEnum.UNREAD.getStat());
        }
        // Specify the order
        Map<String, Integer> ans = new LinkedHashMap<>();
        initCnt(NotifyTypeEnum.COMMENT, map, ans);
        initCnt(NotifyTypeEnum.REPLY, map, ans);
        initCnt(NotifyTypeEnum.PRAISE, map, ans);
        initCnt(NotifyTypeEnum.COLLECT, map, ans);
        initCnt(NotifyTypeEnum.FOLLOW, map, ans);
        initCnt(NotifyTypeEnum.SYSTEM, map, ans);
        return ans;
    }

    private void initCnt(NotifyTypeEnum type, Map<Integer, Integer> map, Map<String, Integer> result) {
        result.put(type.name().toLowerCase(), map.getOrDefault(type.getType(), 0));
    }

    @Override
    public void saveArticleNotify(UserFootDO foot, NotifyTypeEnum notifyTypeEnum) {
        NotifyMsgDO msg = new NotifyMsgDO().setRelatedId(foot.getDocumentId())
                .setNotifyUserId(foot.getDocumentUserId())
                .setOperateUserId(foot.getUserId())
                .setType(notifyTypeEnum.getType())
                .setState(NotifyStatEnum.UNREAD.getStat())
                .setMsg("");
        NotifyMsgDO record = notifyMsgDao.getByUserIdRelatedIdAndType(msg);
        if (record == null) {
            // If there is no corresponding notification already, save it; because a user can repeatedly like or unlike an article, but we only want to notify once
            notifyMsgDao.save(msg);
        }
    }
}


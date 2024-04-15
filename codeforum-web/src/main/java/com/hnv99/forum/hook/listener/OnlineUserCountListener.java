package com.hnv99.forum.hook.listener;

import com.hnv99.forum.core.util.SpringUtil;
import com.hnv99.forum.service.statistics.service.UserStatisticService;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Implements real-time user count statistics by listening to sessions.
 */
@WebListener
public class OnlineUserCountListener implements HttpSessionListener {

    /**
     * When a new session is created, increment the online user count by 1.
     *
     * @param se HttpSessionEvent
     */
    public void sessionCreated(HttpSessionEvent se) {
        HttpSessionListener.super.sessionCreated(se);
        SpringUtil.getBean(UserStatisticService.class).incOnlineUserCnt(1);
    }

    /**
     * When a session expires, decrement the online user count by 1.
     *
     * @param se HttpSessionEvent
     */
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSessionListener.super.sessionDestroyed(se);
        SpringUtil.getBean(UserStatisticService.class).incOnlineUserCnt(-1);
    }
}


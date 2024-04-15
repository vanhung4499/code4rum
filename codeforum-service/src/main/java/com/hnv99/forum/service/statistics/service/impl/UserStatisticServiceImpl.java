package com.hnv99.forum.service.statistics.service.impl;

import com.hnv99.forum.service.statistics.service.UserStatisticService;
import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * User statistics service implementation
 */
@Service
public class UserStatisticServiceImpl implements UserStatisticService {

    /**
     * For single-server scenarios, local variables can be used directly for counting.
     * For clustered scenarios, consider using a Redis sorted set (zset) to implement cluster-wide online user count statistics.
     */
    private AtomicInteger onlineUserCnt = new AtomicInteger(0);

    /**
     * Add online user count
     *
     * @param add positive number to add online user count; negative number to decrease online user count
     * @return the updated online user count
     */
    public int incOnlineUserCnt(int add) {
        return onlineUserCnt.addAndGet(add);
    }

    /**
     * Get the number of online users
     *
     * @return the number of online users
     */
    public int getOnlineUserCnt() {
        return onlineUserCnt.get();
    }
}


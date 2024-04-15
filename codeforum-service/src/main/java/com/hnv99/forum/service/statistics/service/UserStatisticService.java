package com.hnv99.forum.service.statistics.service;

/**
 * User statistics service
 */
public interface UserStatisticService {
    /**
     * Add online user count
     *
     * @param add positive number to add online user count; negative number to decrease online user count
     * @return the updated online user count
     */
    int incOnlineUserCnt(int add);

    /**
     * Get the number of online users
     *
     * @return the number of online users
     */
    int getOnlineUserCnt();
}

package com.hnv99.forum.service.user.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hnv99.forum.service.user.repository.entity.UserDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * User login mapper interface
 */
public interface UserMapper extends BaseMapper<UserDO> {
    /**
     * Query by third-party unique ID
     *
     * @param accountId
     * @return
     */
    @Select("select * from user where third_account_id = #{account_id} limit 1")
    UserDO getByThirdAccountId(@Param("account_id") String accountId);


    /**
     * Traverse user IDs
     *
     * @param offsetUserId
     * @param size
     * @return
     */
    @Select("select id from user where id > #{offsetUserId} order by id asc limit #{size}")
    List<Long> getUserIdsOrderByIdAsc(@Param("offsetUserId") Long offsetUserId, @Param("size") Long size);
}


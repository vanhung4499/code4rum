package com.hnv99.forum.service.user.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hnv99.forum.api.model.vo.PageParam;
import com.hnv99.forum.api.model.vo.user.dto.ArticleFootCountDTO;
import com.hnv99.forum.api.model.vo.user.dto.SimpleUserInfoDTO;
import com.hnv99.forum.api.model.vo.user.dto.UserFootStatisticDTO;
import com.hnv99.forum.service.user.repository.entity.UserFootDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * User footprint mapper interface
 */
public interface UserFootMapper extends BaseMapper<UserFootDO> {

    /**
     * Query article count information
     *
     * @param articleId
     * @return
     */
    ArticleFootCountDTO countArticleByArticleId(@Param("articleId") Long articleId);

    /**
     * Query article statistics by author
     *
     * @param author
     * @return
     */
    ArticleFootCountDTO countArticleByUserId(@Param("userId") Long author);

    /**
     * Query total article reads by author
     *
     * @param author
     * @return
     */
    Integer countArticleReadsByUserId(@Param("userId") Long author);

    /**
     * Query the list of articles collected by the user
     *
     * @param userId
     * @param pageParam
     * @return
     */
    List<Long> listCollectedArticlesByUserId(@Param("userId") Long userId, @Param("pageParam") PageParam pageParam);


    /**
     * Query the list of articles read by the user
     *
     * @param userId
     * @param pageParam
     * @return
     */
    List<Long> listReadArticleByUserId(@Param("userId") Long userId, @Param("pageParam") PageParam pageParam);

    /**
     * Query the like list of the article
     *
     * @param documentId
     * @param type
     * @param size
     * @return
     */
    List<SimpleUserInfoDTO> listSimpleUserInfosByArticleId(@Param("documentId") Long documentId,
                                                           @Param("type") Integer type,
                                                           @Param("size") int size);


    UserFootStatisticDTO getFootCount();
}


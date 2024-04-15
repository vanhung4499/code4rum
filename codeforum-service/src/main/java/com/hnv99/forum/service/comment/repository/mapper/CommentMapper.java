package com.hnv99.forum.service.comment.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hnv99.forum.service.comment.repository.entity.CommentDO;
import org.apache.ibatis.annotations.Param;
import java.util.Map;

/**
 * Comment Mapper Interface
 */
public interface CommentMapper extends BaseMapper<CommentDO> {
    Map<String, Object> getHotTopCommentId(@Param("articleId") Long articleId);
}


package com.hnv99.forum.service.comment.converter;

import com.hnv99.forum.api.model.vo.comment.CommentSaveReq;
import com.hnv99.forum.api.model.vo.comment.dto.BaseCommentDTO;
import com.hnv99.forum.api.model.vo.comment.dto.SubCommentDTO;
import com.hnv99.forum.api.model.vo.comment.dto.TopCommentDTO;
import com.hnv99.forum.service.comment.repository.entity.CommentDO;

import java.util.ArrayList;

/**
 * Comment Converter
 */
public class CommentConverter {

    /**
     * Convert CommentSaveReq to CommentDO
     *
     * @param req The CommentSaveReq object
     * @return The converted CommentDO object
     */
    public static CommentDO toDo(CommentSaveReq req) {
        if (req == null) {
            return null;
        }
        CommentDO commentDO = new CommentDO();
        commentDO.setId(req.getCommentId());
        commentDO.setArticleId(req.getArticleId());
        commentDO.setUserId(req.getUserId());
        commentDO.setContent(req.getCommentContent());
        commentDO.setParentCommentId(req.getParentCommentId() == null ? 0L : req.getParentCommentId());
        commentDO.setTopCommentId(req.getTopCommentId() == null ? 0L : req.getTopCommentId());
        return commentDO;
    }

    private static <T extends BaseCommentDTO> void parseDto(CommentDO comment, T sub) {
        sub.setCommentId(comment.getId());
        sub.setUserId(comment.getUserId());
        sub.setCommentContent(comment.getContent());
        sub.setCommentTime(comment.getCreateTime().getTime());
        sub.setPraiseCount(0);
    }

    /**
     * Convert CommentDO to TopCommentDTO
     *
     * @param commentDO The CommentDO object
     * @return The converted TopCommentDTO object
     */
    public static TopCommentDTO toTopDto(CommentDO commentDO) {
        TopCommentDTO dto = new TopCommentDTO();
        parseDto(commentDO, dto);
        dto.setChildComments(new ArrayList<>());
        return dto;
    }

    /**
     * Convert CommentDO to SubCommentDTO
     *
     * @param comment The CommentDO object
     * @return The converted SubCommentDTO object
     */
    public static SubCommentDTO toSubDto(CommentDO comment) {
        SubCommentDTO sub = new SubCommentDTO();
        parseDto(comment, sub);
        return sub;
    }
}

package com.hnv99.forum.service.comment.service;

import com.hnv99.forum.api.model.vo.comment.CommentSaveReq;

/**
 * Comment Service Interface
 */
public interface CommentWriteService {

    /**
     * Update/Save Comment
     *
     * @param commentSaveReq The CommentSaveReq object
     * @return The ID of the saved comment
     */
    Long saveComment(CommentSaveReq commentSaveReq);

    /**
     * Delete Comment
     *
     * @param commentId The ID of the comment to delete
     * @param userId    The ID of the user performing the deletion
     * @throws Exception If an error occurs during deletion
     */
    void deleteComment(Long commentId, Long userId);

}

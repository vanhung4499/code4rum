package com.hnv99.forum.api.model.vo.comment.dto;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Comment tree structure
 */
@Data
public class BaseCommentDTO implements Comparable<BaseCommentDTO> {

    /**
     * User ID
     */
    private Long userId;

    /**
     * Username
     */
    private String userName;

    /**
     * User photo
     */
    private String userPhoto;

    /**
     * Comment time
     */
    private Long commentTime;

    /**
     * Comment content
     */
    private String commentContent;

    /**
     * Comment ID
     */
    private Long commentId;

    /**
     * Number of likes
     */
    private Integer praiseCount;

    /**
     * True indicates that it has been liked
     */
    private Boolean praised;

    @Override
    public int compareTo(@NotNull BaseCommentDTO o) {
        return Long.compare(o.getCommentTime(), this.commentTime);
    }
}


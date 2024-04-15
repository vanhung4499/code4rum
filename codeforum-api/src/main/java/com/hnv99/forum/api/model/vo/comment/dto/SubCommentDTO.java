package com.hnv99.forum.api.model.vo.comment.dto;

import lombok.Data;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

/**
 * Comment tree structure
 */
@Data
@ToString(callSuper = true)
public class SubCommentDTO extends BaseCommentDTO {

    /**
     * Parent comment content
     */
    private String parentContent;


    @Override
    public int compareTo(@NotNull BaseCommentDTO o) {
        return Long.compare(this.getCommentTime(), o.getCommentTime());
    }
}


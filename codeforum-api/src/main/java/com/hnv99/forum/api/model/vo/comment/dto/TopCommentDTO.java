package com.hnv99.forum.api.model.vo.comment.dto;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Comment tree structure
 */
@Data
public class TopCommentDTO extends BaseCommentDTO {
    /**
     * Number of comments
     */
    private Integer commentCount;

    /**
     * Child comments
     */
    private List<SubCommentDTO> childComments;

    public List<SubCommentDTO> getChildComments() {
        if (childComments == null) {
            childComments = new ArrayList<>();
        }
        return childComments;
    }

    @Override
    public int compareTo(@NotNull BaseCommentDTO o) {
        return Long.compare(o.getCommentTime(), this.getCommentTime());
    }
}


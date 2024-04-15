package com.hnv99.forum.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum for comment status
 */
@Getter
@AllArgsConstructor
public enum CommentStatEnum {

    NOT_COMMENT(0, "Not Commented"),
    COMMENT(1, "Commented"),
    DELETE_COMMENT(2, "Deleted Comment");

    private final Integer code;
    private final String desc;

    public static CommentStatEnum fromCode(Integer code) {
        for (CommentStatEnum value : CommentStatEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return CommentStatEnum.NOT_COMMENT;
    }
}


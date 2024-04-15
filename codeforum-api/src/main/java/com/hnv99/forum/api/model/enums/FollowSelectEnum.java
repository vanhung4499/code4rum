package com.hnv99.forum.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum for user follow options
 */
@Getter
@AllArgsConstructor
public enum FollowSelectEnum {

    FOLLOW("follow", "Following List"),
    FANS("fans", "Fans List");

    private final String code;
    private final String desc;

    public static FollowSelectEnum fromCode(String code) {
        for (FollowSelectEnum value : FollowSelectEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return FollowSelectEnum.FOLLOW;
    }
}


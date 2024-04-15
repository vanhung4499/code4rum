package com.hnv99.forum.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum for follow types
 */
@Getter
@AllArgsConstructor
public enum FollowTypeEnum {

    FOLLOW("follow", "Users I Follow"),
    FANS("fans", "My Followers");

    private final String code;
    private final String desc;

    public static FollowTypeEnum fromCode(String code) {
        for (FollowTypeEnum value : FollowTypeEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return FollowTypeEnum.FOLLOW;
    }
}

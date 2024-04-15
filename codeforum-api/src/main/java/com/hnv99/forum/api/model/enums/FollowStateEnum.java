package com.hnv99.forum.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum for follow status
 */
@Getter
@AllArgsConstructor
public enum FollowStateEnum {

    EMPTY(0, ""),
    FOLLOW(1, "Follow"),
    CANCEL_FOLLOW(2, "Unfollow");

    private final Integer code;
    private final String desc;

    public static FollowStateEnum fromCode(Integer code) {
        for (FollowStateEnum value : FollowStateEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return FollowStateEnum.EMPTY;
    }
}

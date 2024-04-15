package com.hnv99.forum.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum for publishing status
 */
@Getter
@AllArgsConstructor
public enum PushStatusEnum {

    OFFLINE(0, "Unpublished"),
    ONLINE(1, "Published"),
    REVIEW(2, "Under Review");

    private final int code;
    private final String desc;

    public static PushStatusEnum fromCode(int code) {
        for (PushStatusEnum statusEnum : PushStatusEnum.values()) {
            if (statusEnum.getCode() == code) {
                return statusEnum;
            }
        }
        return PushStatusEnum.OFFLINE;
    }
}


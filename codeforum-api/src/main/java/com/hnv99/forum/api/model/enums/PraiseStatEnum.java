package com.hnv99.forum.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum for praise status
 */
@Getter
@AllArgsConstructor
public enum PraiseStatEnum {

    NOT_PRAISE(0, "Not Liked"),
    PRAISE(1, "Liked"),
    CANCEL_PRAISE(2, "Cancel Like");

    private final Integer code;
    private final String desc;

    public static PraiseStatEnum fromCode(Integer code) {
        for (PraiseStatEnum value : PraiseStatEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return PraiseStatEnum.NOT_PRAISE;
    }
}

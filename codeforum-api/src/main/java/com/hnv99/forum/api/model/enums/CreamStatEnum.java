package com.hnv99.forum.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum for featured status
 */
@Getter
@AllArgsConstructor
public enum CreamStatEnum {

    NOT_CREAM(0, "Not Featured"),
    CREAM(1, "Featured");

    private final Integer code;
    private final String desc;

    public static CreamStatEnum fromCode(Integer code) {
        for (CreamStatEnum value : CreamStatEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return CreamStatEnum.NOT_CREAM;
    }
}


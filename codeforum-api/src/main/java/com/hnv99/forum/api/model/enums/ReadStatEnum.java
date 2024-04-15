package com.hnv99.forum.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum for read status
 */
@Getter
@AllArgsConstructor
public enum ReadStatEnum {

    NOT_READ(0, "Unread"),
    READ(1, "Read");

    private final Integer code;
    private final String desc;

    public static ReadStatEnum fromCode(Integer code) {
        for (ReadStatEnum value : ReadStatEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return ReadStatEnum.NOT_READ;
    }
}

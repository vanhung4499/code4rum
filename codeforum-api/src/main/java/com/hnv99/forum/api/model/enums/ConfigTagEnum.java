package com.hnv99.forum.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum for configuration types
 */
@Getter
@AllArgsConstructor
public enum ConfigTagEnum {

    EMPTY(0, ""),
    HOT(1, "Hot"),
    OFFICIAL(2, "Official"),
    COMMENT(3, "Recommended"),
    ;

    private final Integer code;
    private final String desc;

    public static ConfigTagEnum fromCode(Integer code) {
        for (ConfigTagEnum value : ConfigTagEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return ConfigTagEnum.EMPTY;
    }
}

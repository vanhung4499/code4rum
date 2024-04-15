package com.hnv99.forum.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum for configuration types
 */
@Getter
@AllArgsConstructor
public enum ConfigTypeEnum {

    EMPTY(0, ""),
    HOME_PAGE(1, "Homepage Banner"),
    SIDE_PAGE(2, "Sidebar Banner"),
    ADVERTISEMENT(3, "Advertisement Banner"),
    NOTICE(4, "Notice"),
    COLUMN(5, "Tutorial"),
    PDF(6, "E-book");

    private final Integer code;
    private final String desc;

    public static ConfigTypeEnum fromCode(Integer code) {
        for (ConfigTypeEnum value : ConfigTypeEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return ConfigTypeEnum.EMPTY;
    }
}


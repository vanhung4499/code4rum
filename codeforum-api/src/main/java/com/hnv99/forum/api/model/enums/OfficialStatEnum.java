package com.hnv99.forum.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum for official status
 */
@Getter
@AllArgsConstructor
public enum OfficialStatEnum {

    NOT_OFFICIAL(0, "Non-official"),
    OFFICIAL(1, "Official");

    private final Integer code;
    private final String desc;

    public static OfficialStatEnum fromCode(Integer code) {
        for (OfficialStatEnum value : OfficialStatEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return OfficialStatEnum.NOT_OFFICIAL;
    }
}


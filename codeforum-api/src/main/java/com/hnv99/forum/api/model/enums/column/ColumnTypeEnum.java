package com.hnv99.forum.api.model.enums.column;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum for publishing status
 */
@Getter
@AllArgsConstructor
public enum ColumnTypeEnum {

    FREE(0, "Free"),
    LOGIN(1, "Login Required"),
    TIME_FREE(2, "Limited Time Free"),
    STAR_READ(3, "Star Reading"),
    ;

    private final int type;
    private final String desc;

    public static ColumnTypeEnum fromCode(int code) {
        for (ColumnTypeEnum status : values()) {
            if (status.getType() == code) {
                return status;
            }
        }
        return ColumnTypeEnum.FREE;
    }
}


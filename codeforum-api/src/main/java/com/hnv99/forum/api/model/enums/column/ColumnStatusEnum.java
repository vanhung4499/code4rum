package com.hnv99.forum.api.model.enums.column;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum for publishing status
 */
@Getter
@AllArgsConstructor
public enum ColumnStatusEnum {

    OFFLINE(0, "Not published"),
    CONTINUE(1, "Ongoing"),
    OVER(2, "Completed");

    private final int code;
    private final String desc;

    public static ColumnStatusEnum fromCode(int code) {
        for (ColumnStatusEnum status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return ColumnStatusEnum.OFFLINE;
    }
}


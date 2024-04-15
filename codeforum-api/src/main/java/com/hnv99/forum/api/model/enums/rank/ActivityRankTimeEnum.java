package com.hnv99.forum.api.model.enums.rank;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum for time periods of activity rankings
 */
@Getter
@AllArgsConstructor
public enum ActivityRankTimeEnum {
    DAY(1, "day"),
    MONTH(2, "month"),
    ;

    private int type;
    private String desc;

    public static ActivityRankTimeEnum nameOf(String name) {
        if (DAY.desc.equalsIgnoreCase(name)) {
            return DAY;
        } else if (MONTH.desc.equalsIgnoreCase(name)) {
            return MONTH;
        }
        return null;
    }
}


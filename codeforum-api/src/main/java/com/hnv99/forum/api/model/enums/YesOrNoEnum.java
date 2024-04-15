package com.hnv99.forum.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum for status
 */
@Getter
@AllArgsConstructor
public enum YesOrNoEnum {

    NO(0, "N", "Không", "no"),
    YES(1, "Y", "Có", "yes");

    private final int code;
    private final String desc;
    private final String vnDesc;
    private final String enDesc;

    public static YesOrNoEnum fromCode(int code) {
        for (YesOrNoEnum yesOrNoEnum : YesOrNoEnum.values()) {
            if (yesOrNoEnum.getCode() == code) {
                return yesOrNoEnum;
            }
        }
        return YesOrNoEnum.NO;
    }

    /**
     * Check if the code represents yes or no, mainly used for cases where the field is not assigned a value
     *
     * @return
     */
    public static boolean equalYN(Integer code) {
        if (code == null) {
            return false;
        }
        return code.equals(YES.code) || code.equals(NO.code);
    }

    /**
     * Check if it is yes
     *
     * @param code
     * @return
     */
    public static boolean isYes(Integer code) {
        if (code == null) {
            return false;
        }
        return YesOrNoEnum.YES.getCode() == code;
    }

}


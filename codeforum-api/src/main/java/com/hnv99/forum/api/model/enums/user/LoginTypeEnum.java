package com.hnv99.forum.api.model.enums.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LoginTypeEnum {
    /**
     * Username + password login
     */
    USER_PWD(0),

    /**
     * Google login
     */
    GOOGLE(1),

    /**
     * Github login
     */
    GITHUB(2),
    ;

    private int type;
}

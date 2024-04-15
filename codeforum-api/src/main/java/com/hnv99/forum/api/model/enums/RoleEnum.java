package com.hnv99.forum.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum RoleEnum {
    NORMAL(0, "General user"),
    ADMIN(1, "Super user"),
    ;

    private int role;

    private String desc;

    public static String role(Integer roleId) {
        if (Objects.equals(roleId, 1)) {
            return ADMIN.name();
        } else {
            return NORMAL.name();
        }
    }
}

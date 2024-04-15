package com.hnv99.forum.api.model.exception;

import com.hnv99.forum.api.model.vo.constants.StatusEnum;

public class ExceptionUtil {

    public static ForumException of(StatusEnum status, Object... args) {
        return new ForumException(status, args);
    }

}

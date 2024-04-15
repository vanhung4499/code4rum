package com.hnv99.forum.api.model.exception;

import com.hnv99.forum.api.model.vo.Status;
import com.hnv99.forum.api.model.vo.constants.StatusEnum;
import lombok.Getter;

/**
 * Business exception
 */
public class ForumAdviceException extends RuntimeException {
    @Getter
    private Status status;

    public ForumAdviceException(Status status) {
        this.status = status;
    }

    public ForumAdviceException(int code, String msg) {
        this.status = Status.newStatus(code, msg);
    }

    public ForumAdviceException(StatusEnum statusEnum, Object... args) {
        this.status = Status.newStatus(statusEnum, args);
    }

}

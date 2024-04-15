package com.hnv99.forum.api.model.exception;

/**
 * Unmatched Exception
 */
public class NoValueInGuavaException extends RuntimeException {
    public NoValueInGuavaException(String msg) {
        super(msg);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}

package com.meituan.mtest;

public class MtestException extends RuntimeException {

    public MtestException(String message) {
        super(message);
    }

    public MtestException(String message, Throwable cause) {
        super(message, cause);
    }

}

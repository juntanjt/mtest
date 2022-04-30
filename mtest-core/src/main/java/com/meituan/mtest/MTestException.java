package com.meituan.mtest;

/**
 *
 * @author Jun Tan
 */
public class MTestException extends RuntimeException {

    /**
     *
     * @param message
     */
    public MTestException(String message) {
        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public MTestException(String message, Throwable cause) {
        super(message, cause);
    }

}

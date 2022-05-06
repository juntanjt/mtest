package com.meituan.mtest.demo.order;

/**
 * @author Jun Tan
 */
public class OrderException extends RuntimeException {

    public static final String ITEM_NOT_EXIST = "ERROR_CODE_123";

    private String errorCode;

    public OrderException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

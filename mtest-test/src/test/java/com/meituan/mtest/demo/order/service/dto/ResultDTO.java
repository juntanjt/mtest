package com.meituan.mtest.demo.order.service.dto;

import lombok.Data;

@Data
public class ResultDTO<T> {

    private boolean success;
    private T value;
    private String errorCode;
    private String errorMessage;

    private ResultDTO() {
    }

    public static <T> ResultDTO<T> of(T value) {
        ResultDTO<T> resultDTO = new ResultDTO<>();
        resultDTO.value = value;
        resultDTO.success = true;
        return resultDTO;
    }

    public static <T> ResultDTO<T> error(String errorCode, String errorMessage) {
        ResultDTO<T> resultDTO = new ResultDTO<>();
        resultDTO.errorCode = errorCode;
        resultDTO.errorMessage = errorMessage;
        resultDTO.success = false;
        return resultDTO;
    }

}

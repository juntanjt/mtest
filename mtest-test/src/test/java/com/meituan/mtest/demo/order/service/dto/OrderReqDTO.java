package com.meituan.mtest.demo.order.service.dto;

import lombok.Data;

@Data
public class OrderReqDTO {

    private Long itemId;
    private Integer itemCount;
    private String address;
    private String telephone;

}

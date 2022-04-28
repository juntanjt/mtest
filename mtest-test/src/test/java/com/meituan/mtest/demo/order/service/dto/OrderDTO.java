package com.meituan.mtest.demo.order.service.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDTO {

    private Long orderId;
    private Long userId;
    private Long itemId;
    private Integer itemCount;
    private BigDecimal amount;
    private String orderName;
    private String address;
    private String telephone;

}

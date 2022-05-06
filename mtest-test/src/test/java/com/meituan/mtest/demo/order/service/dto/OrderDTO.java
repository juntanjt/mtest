package com.meituan.mtest.demo.order.service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

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
    private Date createTime;

}

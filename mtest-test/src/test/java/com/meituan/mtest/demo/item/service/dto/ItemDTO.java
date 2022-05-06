package com.meituan.mtest.demo.item.service.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemDTO {

    private Long itemId;
    private String itemName;
    private BigDecimal price;

}

package com.meituan.mtest.demo.order.dao;

public interface OrderDAO {

    int insert(OrderDO orderDO);

    OrderDO queryById(Long orderId);

}

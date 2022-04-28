package com.meituan.mtest.demo.order.service;

import com.meituan.mtest.demo.order.service.dto.OrderDTO;
import com.meituan.mtest.demo.order.service.dto.OrderReqDTO;

public interface OrderService {

    OrderDTO createOrder(Long userId, OrderReqDTO orderReqDTO);

}

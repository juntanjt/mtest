package com.meituan.mtest.demo.order.service;

import com.meituan.mtest.demo.order.service.dto.OrderDTO;
import com.meituan.mtest.demo.order.service.dto.OrderReqDTO;
import com.meituan.mtest.demo.order.service.dto.ResultDTO;

public interface OrderService {

    ResultDTO<Long> createOrder(Long userId, OrderReqDTO orderReqDTO);

    ResultDTO<OrderDTO> queryOrder(Long userId, Long orderId);

}

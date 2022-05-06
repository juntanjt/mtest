package com.meituan.mtest.demo.order.service.impl;

import com.meituan.mtest.demo.item.service.ItemService;
import com.meituan.mtest.demo.item.service.dto.ItemDTO;
import com.meituan.mtest.demo.order.OrderException;
import com.meituan.mtest.demo.order.dao.OrderDAO;
import com.meituan.mtest.demo.order.dao.OrderDO;
import com.meituan.mtest.demo.order.service.OrderService;
import com.meituan.mtest.demo.order.service.dto.OrderDTO;
import com.meituan.mtest.demo.order.service.dto.OrderReqDTO;
import com.meituan.mtest.demo.order.service.dto.ResultDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Service("orderService")
public class OrderServiceImpl implements OrderService {

    @Resource
    private ItemService itemService;
    @Resource
    private OrderDAO orderDAO;

    @Override
    public ResultDTO<Long> createOrder(Long userId, OrderReqDTO orderReqDTO) {
        ItemDTO itemDTO = itemService.queryItemById(orderReqDTO.getItemId());

        if (itemDTO==null) {
            throw new OrderException(OrderException.ITEM_NOT_EXIST, "item is not exists, itemId="+orderReqDTO.getItemId());
        }

        OrderDO orderDO = new OrderDO();
        orderDO.setUserId(userId);
        orderDO.setItemId(orderReqDTO.getItemId());
        orderDO.setItemCount(orderReqDTO.getItemCount());
        // amount
        orderDO.setAmount(itemDTO.getPrice().multiply(BigDecimal.valueOf(orderReqDTO.getItemCount())));
        orderDO.setOrderName(itemDTO.getItemName());
        orderDO.setAddress(orderReqDTO.getAddress());
        orderDO.setTelephone(orderReqDTO.getTelephone());

        int count = orderDAO.insert(orderDO);
        if (count != 1) {
            throw new RuntimeException("orderDAO.insert failed");
        }

        return ResultDTO.of(orderDO.getId());
    }

    @Override
    public ResultDTO<OrderDTO> queryOrder(Long userId, Long orderId) {
        OrderDO orderDO = orderDAO.queryById(orderId);
        if (orderDO == null) {
            return ResultDTO.error("", "");
        }

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderId(orderId);
        orderDTO.setUserId(orderDO.getUserId());
        orderDTO.setItemId(orderDO.getItemId());
        orderDTO.setItemCount(orderDO.getItemCount());
        orderDTO.setAmount(orderDO.getAmount());
        orderDTO.setOrderName(orderDO.getOrderName());
        orderDTO.setAddress(orderDO.getAddress());
        orderDTO.setTelephone(orderDO.getTelephone());
        orderDTO.setCreateTime(orderDO.getCreateTime());
        return ResultDTO.of(orderDTO);
    }

}

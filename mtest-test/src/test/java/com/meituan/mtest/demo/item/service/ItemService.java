package com.meituan.mtest.demo.item.service;

import com.meituan.mtest.demo.item.service.dto.ItemDTO;

public interface ItemService {

    ItemDTO queryItemById(Long itemId);

}

package com.wman.service;

import com.wman.entity.Order;
import com.wman.utils.Result;

import java.util.List;

/**
 * @ClassName OrderService
 * @Author wman
 * @Date 2021/9/5
 */
public interface OrderService {

    Result insert(Order order);

    Result update(Boolean isHandled, Integer id);

    List<Order> select();

    Result selectByOpenid(String openid);

    Result delete(Integer id);

    Result selectById(Integer id);
}

package com.wman.mapper;

import com.wman.entity.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderMapper {

    int insert(Order order);

    int update(Boolean isHandled, Integer id);

    List<Order> select();

    List<Order> selectByOpenid(String openid);

    int delete(Integer id);

    Order selectById(Integer id);

}

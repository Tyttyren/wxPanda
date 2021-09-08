package com.wman.mapper;

import com.wman.entity.OrderManage;
import org.springframework.stereotype.Repository;

import java.util.List;

//订单的接口
@Repository
public interface OrderManageMapper {

    int insert(OrderManage manage);

    List<OrderManage> select();

    String selectByOpenID(String openid);

    //通过商户号来查询结果是否存在
    String selectOrder(String orderNum);

    //存储数据
    int insertTransaction(String transaction,String orderNum);
}

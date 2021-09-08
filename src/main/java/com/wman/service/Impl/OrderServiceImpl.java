package com.wman.service.Impl;

import com.wman.entity.Order;
import com.wman.mapper.OrderMapper;
import com.wman.service.OrderService;
import com.wman.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName OrderServiceImpl
 * @Author wman
 * @Date 2021/9/5
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;

    @Override
    public Result insert(Order order) {
        // 判断传入参数是否为空
        if (null == order.getBuilding() || null==order.getCreateByOpenid()  || null==order.getPark() || order.getPrice()<=0 || null==order.getRoom()){
            return Result.fail("传递参数不可以为空");
        }
        return orderMapper.insert(order)>0 ? Result.success("插入成功") : Result.fail("插入失败");
    }

    @Override
    public Result update(Boolean isHandled, Integer id) {
        return orderMapper.update(isHandled, id)>0 ? Result.success("更新成功") : Result.fail("更新失败");
    }

    @Override
    public List<Order> select() {
        return orderMapper.select();
    }

    @Override
    public Result selectByOpenid(String openid) {
        if (null == openid){
            return Result.fail("传递参数不能为空");
        }
        List<Order> order = orderMapper.selectByOpenid(openid);
        return Result.success("查询成功",order);
    }

    @Override
    public Result delete(Integer id) {
        return orderMapper.delete(id)>0 ? Result.success("删除成功") : Result.fail("删除失败");
    }

    public Result selectById(Integer id) {
        return Result.success("查询成功",orderMapper.selectById(id));
    }
}

package com.wman.controller;

import com.alibaba.fastjson.JSONObject;
import com.wman.entity.Order;
import com.wman.service.Impl.OrderServiceImpl;
import com.wman.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName OrderController
 * @Author wman
 * @Date 2021/9/5
 */
@RestController
@RequestMapping("/order")
@Api("订单接口")
public class OrderController {
    @Autowired
    OrderServiceImpl orderService;

    @ApiOperation("新增订单")
    @PostMapping("/insert")
    public Result insert(@RequestBody Order order){
        return orderService.insert(order);
    }

    @ApiOperation("更新订单")
    @PostMapping("/update")
    public Result update(@RequestParam("isHandled") Boolean isHandled, @RequestParam("id") Integer id){
        return orderService.update(isHandled, id);
    }

    @ApiOperation("删除订单")
    @PostMapping("/delete")
    public Result delete(@RequestParam("id") Integer id){
        return orderService.delete(id);
    }

    @ApiOperation("查询所有订单")
    @PostMapping("/select")
    public Result select(){
        return Result.success("查询成功",orderService.select());
    }

    @ApiOperation("通过openid查询订单")
    @GetMapping("/selByOp")
    public Result selectByOpenid(@RequestParam("opnid") String openid){
        return orderService.selectByOpenid(openid);
    }

    @ApiOperation("通过id查询订单")
    @PostMapping("/selById")
    public Result selectById(@RequestParam("id") Integer id){
        return Result.success("查询成功",orderService.selectById(id));
    }

    @ApiOperation("Json数据转换接口")
    @PostMapping("/json")
    public Result JsonData(@RequestBody JSONObject jsonObject){
        List<Object> data = (List<Object>) jsonObject.get("data");
        System.out.println(data.size());
        for (Object datum : data) {
            System.out.println(datum);
        }
        return null;
    }

}

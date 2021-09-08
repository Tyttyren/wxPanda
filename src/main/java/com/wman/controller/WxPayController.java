package com.wman.controller;


import com.alibaba.fastjson.JSONObject;
import com.wman.service.PayService;
import com.wman.utils.Result;
import com.wman.utils.signUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/WeChatPay")
public class WxPayController {

    @Autowired//支付的业务
    PayService payService;
    //下单的接口
    @PostMapping("/createOrder")
    public Result placeAnOrder(String code, double fee, String need,String openid){
        try {
        String jsApiPath="https://api.mch.weixin.qq.com/v3/pay/partner/transactions/jsapi";
        JSONObject object=payService.createOrder(code,fee,need,openid);
        JSONObject returnObject=null;
        //先要向平台发送post请求
            returnObject=signUtils.getWeChatURL(jsApiPath,"Post",object);

        if(returnObject.containsKey("code")) return Result.fail("支付请求失败");

             //构建发送到前端的json数据
            JSONObject jsonObject = payService.sendToFace(returnObject.getString("prepay_id"));

            //获得prepay_id之后准备将这个封装好发送到前端
            return Result.success("拉起支付请求",jsonObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.fail("支付发生位置错误");
    }

    @GetMapping("/return")
    public void getReturn(JSONObject js){//获取支付的结果

        System.out.println("微信返回的结果:"+js.toString());
        //获取prepayid
        String prepay_id = (String) js.get("prepay_id");

    }

    @PostMapping("/seaarchResult")
    public Result searchResult(String openid){//查询订单
        //将openid处理
        JSONObject object = payService.queryOrder(openid);
        return object==null?Result.fail("查询失败"):Result.success("查询成功",object);
    }

     //关闭订单
    @GetMapping("/cansel")
    public Result cansel(String orderManage){
        Result result = payService.cancelOrder(orderManage);
        if(result.getData()==null) return result;
        else {
            JSONObject jsonObject=(JSONObject) result.getData();
            System.out.println(jsonObject.toString());
        }
        return null;
    }
}

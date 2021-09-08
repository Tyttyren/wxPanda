package com.wman.service;

import com.alibaba.fastjson.JSONObject;
import com.wman.utils.Result;

public interface PayService {

    //下单的接口
    JSONObject createOrder(String code, double fee, String need,String openid);

    //用户查询的接口
    JSONObject queryOrder(String id);

    //关闭订单
    Result cancelOrder(String orderManage);

    //构建发送到前端的json数据
    JSONObject sendToFace(/*先前获得的prepay_id*/String prepayID);

    //获取支付的结果
    void payResult(JSONObject jsonObject);
}

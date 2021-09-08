package com.wman.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import com.wman.entity.OrderManage;
import com.wman.mapper.OrderManageMapper;
import com.wman.mapper.OrderMapper;
import com.wman.service.PayService;
import com.wman.staticClass.necessaryID;
import com.wman.utils.AESCodeUtil;
import com.wman.utils.HttpUtil;
import com.wman.utils.Result;
import com.wman.utils.signUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;

@Service
public class PayServiceImpl implements PayService {

    @Autowired
    OrderMapper mapper;
    @Autowired
    OrderManageMapper orderManageMapper;


    @Override
    public JSONObject createOrder(String code,double fee,String need,String openid) {
        String orderID=signUtils.redomNumber();
        JSONObject object=new JSONObject();
        object.put("sp_appid", necessaryID.appid);
        object.put("sp_mchid",necessaryID.mchid);
        object.put("sub_mchid",necessaryID.mchid);
        object.put("description",need);//商品描述
        object.put("out_trade_no", orderID);//生成一个订单号
        object.put("notify_url", "https://localhost:8080/return");//测试
        //将订单号存储到数据库中
        OrderManage order=new OrderManage(openid,orderID);
        orderManageMapper.insert(order);
        JSONObject amount=new JSONObject();//订单号中创建金额
        //将fee作为64位数
        long rmb= (long) (fee*100);
         amount.put("total",rmb);
         amount.put("currency","CNY");
        JSONObject payer=new JSONObject();
        payer.put("sp_openid","opIbz5D30gMzE-NcbW03fio6LV4o");
        object.put("amount",amount);
        object.put("payer",payer);
        return object;
    }

    //通过前端发送过来的openid来获取orderid从而进行订单的查询
    public JSONObject queryOrder(String opoenid) {
        try {
            String orderid=orderManageMapper.selectByOpenID(opoenid);
            String url="https://api.mch.weixin.qq.com/v3/pay/partner/transactions/id/";
            String[] rote=new String[]{orderid,"?sp_mchid="+necessaryID.sp_mchid,"&sub_mchid="+necessaryID.mchid};//将get请求的路劲参数封装号
            url=setUrl(url,rote);
            JSONObject get = signUtils.getWeChatURL(url, "Get", null);//向服务器发送请求
            return get;
        }catch (IOException e){
            e.printStackTrace();
        }
       return null;
    }

    @Override
    public Result cancelOrder(String orderManage) {//取消订单
        try {
            //将商品的订单封装好
            JSONObject js = new JSONObject();
            js.put("sp_mchid", necessaryID.sp_mchid);
            js.put("sub_mchid", necessaryID.mchid);
            js.put("out_trade_no", orderManage);
            String url = "https://api.mch.weixin.qq.com/v3/pay/partner/transactions/out-trade-no/" + orderManage + "/close";
            JSONObject post = signUtils.getWeChatURL(url, "Post", js);
            if (post.containsKey("code") && post.get("code") == "204") return Result.success("成功取消");
            return Result.fail("取消失败", post);
        }catch (IOException e) {
            e.printStackTrace();
        }return Result.fail("加密出现问题");
    }

    @Override
    public JSONObject sendToFace(String prepayID) {
        try {
        long time_stap= System.currentTimeMillis();
        String 	nonceStr= signUtils.redomNumber();
        //生成一个签名
        StringBuilder paySign=new StringBuilder();
        paySign.append(necessaryID.appid + "\n").
                append(time_stap).append("\n").
                append(nonceStr).append("\n").
                append("prepay_id=").append(prepayID).append("\n");
        //进行RSA签名
            Signature signature=Signature.getInstance("SHA256withRSA");
          signature.initSign(PemUtil.loadPrivateKey(new FileInputStream(new File(necessaryID.fileKey))));
          signature.update(paySign.toString().getBytes(StandardCharsets.UTF_8));
        //封装一个json数据，然后将它发送到前端
        JSONObject js=new JSONObject();
        js.put("appId",necessaryID.appid);
        js.put("timeStamp", Long.toString(time_stap));
        js.put("nonceStr",nonceStr);
        js.put("package",prepayID);
        js.put("signType","RSA");
        js.put("paysign",paySign.toString());
        return js;
        } catch (NoSuchAlgorithmException | FileNotFoundException | SignatureException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return (JSONObject) new JSONObject().put("fail","加密出现问题");
    }

    @Override
    public void payResult(JSONObject jsonObject) {//获取支付的结果
        //获取resource结果
        JSONObject resource = (JSONObject) jsonObject.get("resource");
        String asa=resource.getString("nonce")+resource.getString("associated_data");
        String serect=resource.getString("ciphertext");
        //用aescode解密
        JSONObject userInfo = AESCodeUtil.getUserInfo(serect, necessaryID.apiKey, asa);
        //获取
        String out_trade_no = userInfo.getString("out_trade_no");
        if(TraisEmpty(out_trade_no)){//判断商户创建的id是否存在，如果存在则说明已经完成支付
        }else {
            //将解密过后的数据存到数据库中
            String transaction_id = userInfo.getString("transaction_id");
            //存储到数据库
            int i = orderManageMapper.insertTransaction(transaction_id, out_trade_no);
           if(i<=0) {
               //向微信发送失败的通知
               JSONObject returnTOWeChat=new JSONObject();
               returnTOWeChat.put("code","失败");
               returnTOWeChat.put("message","未知原因");
               //
               try {
                   HttpUtil.sendHttpPOSTRequest(new StringBuilder("https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_7&index=8"),returnTOWeChat);
               } catch (IOException e) {
                   e.printStackTrace();
               }
               return;
           }
        }
         //向微信服务器发送回应,向https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_7&index=8
        //发送请求
        JSONObject returnTOWeChat=new JSONObject();
        returnTOWeChat.put("code","success");
        returnTOWeChat.put("message","成功");
        try {
            HttpUtil.sendHttpPOSTRequest(new StringBuilder("https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_7&index=8"),returnTOWeChat);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private static String setUrl(String url,String... strings){
        StringBuilder sb=new StringBuilder();
        sb.append(url);
        for(String temp:strings){
            sb.append(temp);
        }
        return sb.toString();
    }

    //判断订单是否已经写入
    private boolean TraisEmpty(String outTradeNo){
        try {
            String s = orderManageMapper.selectOrder(outTradeNo);
            return true;
        }catch (NullPointerException e){
            return false;
        }
    }
}

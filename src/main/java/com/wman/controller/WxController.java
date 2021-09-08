package com.wman.controller;

import com.alibaba.fastjson.JSONObject;
import com.wman.entity.WxUser;
import com.wman.service.Impl.WxServiceImpl;
import com.wman.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.List;

/**
 * @ClassName WxController
 * @Author wman
 * @Date 2021/9/3
 */
@RestController
@RequestMapping("wx")
public class WxController {
    @Autowired
    private WxServiceImpl wxService;


    @GetMapping("/allUser")
    //@Secured({"ROLE_ADMIN","ROLE_SUPERADMIN"})
    public Result findAllUser(){
        List<WxUser> allUser = wxService.findAllUser();
        return Result.success("成功",allUser);
    }

   @PostMapping("/uApply")
    //微信支付,前端封装好相应的json对象
   public Result apply(@RequestBody JSONObject js) throws IOException, SignatureException, NoSuchAlgorithmException, InvalidKeyException {
//       StringBuilder sb=new StringBuilder("https://api.mch.weixin.qq.com/v3/certificates");
//       JSONObject jsonObject1 = wxService.hsndleJS(js);
////       JSONObject jsonObject = HttpUtil.sendHttpPOSTRequestWheChat(sb,jsonObject1);
//       JSONObject jsonObject=HttpUtil.sendHttpGETRequestW(sb);
//       System.out.println(jsonObject.toString());
//       return jsonObject.containsKey("prepay_id")?Result.success("支付成功"):
//               Result.fail("支付失败",jsonObject.getDate("message"));

      return null;
   }
}

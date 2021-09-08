package com.wman.service;

import com.alibaba.fastjson.JSONObject;
import com.wman.entity.WxUser;
import com.wman.utils.Result;

import java.util.List;

public interface WxService {
    /**
     * 查询所有的用户数据
     * @return
     */
    List<WxUser> findAllUser();

    /**
     * 通过username即openid查询得到用户
     * @param username
     * @return
     */
    WxUser findByusername(String username);

    /**
     * 微信用户登录
     * @param code
     * @return
     */
    Result wxlogin(String code, String encryptedData, String iv, String nickName,String address, String avatarUrl, Integer gender);

    /**
     * 系统用户登录
     * @param username
     * @param password
     * @return
     */
    Result login(String username, String password);

    //处理前端发送过来的json请求
    JSONObject hsndleJS(JSONObject jsonObject);
}

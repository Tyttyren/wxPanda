package com.wman.controller;

import com.wman.contents.MessageConstant;
import com.wman.service.Impl.WxServiceImpl;
import com.wman.utils.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName LoginController
 * @Author wman
 * @Date 2021/9/3
 */
@RestController
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private WxServiceImpl wxService;

    /**
     * 微信用户登录
     * @param code
     * @param encryptedData
     * @param iv
     * @param nickName
     * @param avatarUrl
     * @param gender
     * @param address
     * @return
     */
    @PostMapping("/wxlogin")
    @ApiOperation(value = "登录", httpMethod = "POST")
    public Result wxlogin(@RequestParam("code") String code,
                          @RequestParam("encrypteData") String encryptedData,
                          @RequestParam("iv")String iv,
                          @RequestParam("nickName")String nickName,
                          @RequestParam("avatarUrl")String avatarUrl,
                          @RequestParam("gender")Integer gender,
                          @RequestParam("address") String address){
        return wxService.wxlogin(code,encryptedData,iv,nickName,address,avatarUrl,gender);
    }

    /**
     * 同一用户登出
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/logout")
    @ApiOperation(value = "退出", httpMethod = "GET")
    public Result logout(HttpServletRequest request, HttpServletResponse response) {
        //清除spring security用户认证信息
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return new Result(true, MessageConstant.LOGOUT_SUCCESS);
    }

    @PostMapping("/login")
    @ApiOperation(value = "系统用户登录", httpMethod = "POST")
    public Result logoin(@RequestParam("username") String username,@RequestParam("password") String password) {
        return wxService.login(username, password);
    }

}

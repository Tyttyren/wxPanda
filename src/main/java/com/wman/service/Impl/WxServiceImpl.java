package com.wman.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.wman.contents.MessageConstant;
import com.wman.entity.Permission;
import com.wman.entity.Role;
import com.wman.entity.WxUser;
import com.wman.mapper.WxMapper;
import com.wman.service.WxService;
import com.wman.utils.AESCodeUtil;
import com.wman.utils.HttpUtil;
import com.wman.utils.JwtTokenUtil;
import com.wman.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @ClassName WxServiceImpl
 * @Author wman
 * @Date 2021/9/3
 */
@Service
@Slf4j
public class WxServiceImpl implements WxService {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PermissionServiceImpl permissionService;

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Value("${wx.appid}")
    private String appid;

    @Value("${wx.secret}")
    private String secret;

    @Value("${wx.sp_mchid}")
    private String sp_mchid;

    @Value("${wx.sub_mchid}")
    private String sub_mchid;
    @Autowired
    private WxMapper wxMapper;

    @Override
    public List<WxUser> findAllUser() {
        List<WxUser> allUser = wxMapper.findAllUser();
        return allUser;
    }

    @Override
    public WxUser findByusername(String username) {
        WxUser byusername = wxMapper.findByusername(username);
        return byusername;
    }

    @Override
    public Result wxlogin(String code, String encryptedData, String iv, String nickName, String address, String avatarUrl, Integer gender) {
        // 首先判断传递来的参数是否为空
        if (code==null || encryptedData==null || iv==null || nickName==null || address==null || avatarUrl==null || gender<0 || gender>2){
            return Result.fail("参数不可以传递null值");
        }
        //1.通过code换取openid和session_key
        //访问微信url
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=wx9c3c249594a50e9f&secret=0cb395476864095ce79ff7ed766b1e51&js_code="+code+"&grant_type=authorization_code";

        StringBuilder url2 = new StringBuilder("https://api.weixin.qq.com/sns/jscode2session");
        url2.append("?appid="+appid).append("&secrect="+secret).append("&js_code="+code).append("&grant_type=authorization_code");
        try {
            //2. 根据请求码获取用户openId
            JSONObject response = HttpUtil.sendHttpGETRequest(new StringBuilder(url));
//            String response = HttpUtil.getResponse(url, map);
            //将字符串转为Json
//            JSONObject object = JSON.parseObject(response);
            //得到openId
            String openId = response.getString("openid");
            //得到sessionKey
            String sessionKey = response.getString("session_key");

            // 判断是否正确获得openid和session_key
            if (openId==null || sessionKey==null)


            if (openId == null) {
                log.warn("openid is null");
                return Result.fail("没有获取到openid");
                //throw new NullPointerException("openid is null");
            }

            if (sessionKey == null) {
                log.warn("sessionKey is null");
                return Result.fail("没有获得session_key");
                //throw new NullPointerException("sessionKey is null");
            }

            // ---------------------start----------------解密手机号,先判空
            if (encryptedData == null || iv == null){
                log.warn("encryptData and iv is null");
                //throw new NullPointerException("encryptData and iv is null");
            }
            JSONObject userInfo = AESCodeUtil.getUserInfo(encryptedData, sessionKey, iv);
            if (userInfo == null) {
                log.warn("userinfo is null");
                throw new NullPointerException("解密的userinfo结果错误，请查看参数");
            }
            String phoneNumber = userInfo.getString("phoneNumber");
            // ---------------------end-------------------解密手机号

            /*   至此已经获得openid，session_key，phoneNumber，nickName，avatarUrl，gender   */

            //3. 根据用户的username即openid去查询是否含有该用户，判断是新增还是更新
            WxUser byusername = wxMapper.findByusername(openId);
            UserDetails userDetails;
            if (byusername != null) {
                // 更新数据库的用户信息
                if (!(byusername.getNickName().equals(nickName)
                        && byusername.getAddress().equals(address)
                        && byusername.getGender().equals(gender)
                        && byusername.getPhoneNumber().equals(phoneNumber)
                        && byusername.getAvatarUrl().equals(avatarUrl))){
                    // 满足以上条件代表用户数据未曾改变(取非)----->更新用户数据
                    //WxUser user = new WxUser();
                    //user.setOpenid(openId);
                    byusername.setNickName(nickName);
                    byusername.setAddress(address);
                    byusername.setGender(gender);
                    byusername.setPhoneNumber(phoneNumber);
                    byusername.setAvatarUrl(avatarUrl);
                    byusername.setUpdateTime(new Date());
                    wxMapper.update(byusername);
                }
                userDetails = userDetailsService.loadUserByUsername(byusername.getUsername());
            }else {
                // 新增用户信息
                WxUser user = new WxUser();
                user.setOpenid(openId);
                user.setNickName(nickName);
                user.setAddress(address);
                user.setGender(gender);
                user.setPhoneNumber(phoneNumber);
                user.setAvatarUrl(avatarUrl);
                user.setCreateTime(new Date());
                user.setUpdateTime(new Date());
                // 默认设置新用户的角色为普通用户
                // 获得权限列表
                List<Permission> permissions = permissionService.findByRoleId(3);
                user.setRole(new Role(3,"ROLE_USER","普通用户",permissions));
                // 将用户数据插入数据库
                wxMapper.insert(user);
                // 将用户信息加载到userDetails里面
                userDetails = userDetailsService.loadUserByUsername(openId);
            }

            //更新security登录用户对象
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            // 根据用户信息生成token，下发小程序
            String token = jwtTokenUtil.generateToken(userDetails);
            Map<String, String> result = new HashMap<>(16);
            log.info("opemid-->"+openId+"session_key--->"+sessionKey+"tokenHead"+tokenHead+"token"+token);
            result.put("openId", openId);
            result.put("sessionKey", sessionKey);
            result.put("tokenHead", tokenHead);
            result.put("token", token);
            //4. 返回用户信息以及微信所需参数 token
            return Result.success(MessageConstant.LOGIN_SUCCESS, result);
        } catch (Exception e) {
            log.info("程序出错： --> {}", e.getMessage());
//            e.printStackTrace();
            return Result.fail("登录失败");
        }
    }

    @Override
    public Result login(String username, String password) {
        // username即openid 密码是id+openid的加密字符串
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (null == userDetails || !passwordEncoder.matches(password, userDetails.getPassword())) {
            return Result.fail("用户名或密码错误！");
        }
        if (!userDetails.isEnabled()) {
            return Result.fail("该帐号已被禁用，请联系管理员！");
        }
        //更新security登录用户对象
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        String token = jwtTokenUtil.generateToken(userDetails);
        Map<String, String> map = new HashMap<>(16);
        map.put("tokenHead", tokenHead);
        map.put("token", token);
        return new Result(true, MessageConstant.LOGIN_SUCCESS, map);
    }

    //处理json
    public JSONObject hsndleJS(JSONObject jsonObject) {
        //将商品Id,商户号码，订单号等封装到一起
        //生成订单号
        String out_trade_no=UUID.randomUUID().toString().replace("-","");
        jsonObject.put("sp_appid",appid);
        jsonObject.put("sp_mchid",sp_mchid);
        jsonObject.put("sub_mchid",sub_mchid);
        jsonObject.put("out_trade_no",out_trade_no);
        return jsonObject;
    }
}

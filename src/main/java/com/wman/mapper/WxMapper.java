package com.wman.mapper;

import com.wman.entity.WxUser;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WxMapper {

    /**
     * 查询全部用户
     * @return
     */
    List<WxUser> findAllUser();

    /**
     * 铜鼓username（或者openid）查询得到user
     * @param username
     */
    WxUser findByusername(String username);

    /**
     * 用戶登錄，插入新的数据
     * @param wxUser
     * @return
     */
    Integer insert(WxUser wxUser);

    void update(WxUser user);
}

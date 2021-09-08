package com.wman.mapper;

import com.wman.entity.Permission;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @ClassName PermissionMapper
 * @Author wman
 * @Date 2021/9/4
 */
@Repository
public interface PermissionMapper {

    /**
     * 通过roleID查询该角色下面的权限
     * @param roleId
     * @return
     */
    List<Permission> findByRoleId(Integer roleId);
}

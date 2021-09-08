package com.wman.service.Impl;

import com.wman.entity.Permission;
import com.wman.mapper.PermissionMapper;
import com.wman.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName PermissionServiceImpl
 * @Author wman
 * @Date 2021/9/4
 */
@Service
public class PermissionServiceImpl implements PermissionService {
    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public List<Permission> findByRoleId(Integer roleId) {
        return permissionMapper.findByRoleId(roleId);
    }
}

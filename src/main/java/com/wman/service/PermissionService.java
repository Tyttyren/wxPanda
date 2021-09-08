package com.wman.service;

import com.wman.entity.Permission;

import java.util.List;

public interface PermissionService {

    List<Permission> findByRoleId(Integer roleId);
}

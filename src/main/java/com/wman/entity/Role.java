package com.wman.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName Role
 * @Author wman
 * @Date 2021/9/3
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    private Integer id;

    @ApiModelProperty("角色标识")
    private String name;

    @ApiModelProperty("角色描述")
    private String remark;

    @ApiModelProperty("角色权限")
    private List<Permission> permissions;
}

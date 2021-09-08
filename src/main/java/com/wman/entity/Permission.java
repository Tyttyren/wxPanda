package com.wman.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName Premission
 * @Author wman
 * @Date 2021/9/3
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Permission {
    private Integer id;

    @ApiModelProperty("权限名称")
    private String name;

    @ApiModelProperty("权限标识")
    private String permission;
}

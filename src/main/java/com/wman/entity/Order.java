package com.wman.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @ClassName Order
 * @Author wman
 * @Date 2021/9/5
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    private Integer id;

    private String createByOpenid;

    private String park;

    private String building;

    private String unit;

    private String floor;

    private String room;

    private Integer price;

    private Boolean isHandled;

    private Date createTime;

    private Date updateTime;

}

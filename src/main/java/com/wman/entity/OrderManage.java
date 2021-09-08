package com.wman.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderManage {
    Integer id;
    String openID;
    String orderNum;
    String transactionId;

    public OrderManage(String openID, String orderNum) {
        this.openID = openID;
        this.orderNum = orderNum;
    }
}

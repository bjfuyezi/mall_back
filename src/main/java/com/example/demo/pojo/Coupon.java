package com.example.demo.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class Coupon {
    private Integer couponId;
    private String type; // shop 或 platform
    private Date startTime;
    private Date endTime;
    private String scope; // JSON 格式
    private double request; // 最低消费
    private double off; // 折扣金额
}

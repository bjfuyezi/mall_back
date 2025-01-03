package com.example.demo.pojo;

import com.example.demo.enums.UserCouponStatus;
import lombok.Data;

import java.util.Date;

@Data
public class UserCoupon {
    private Integer userCouponId;
    private Integer userId;
    private Integer couponId;
    private UserCouponStatus userCouponStatus;
    private Date claimTime;

    // 新增 shopId 属性
    private Integer shopId;             // 店铺ID

    public UserCoupon(Integer userId, Integer couponId, UserCouponStatus userCouponStatus, Date claimTime) {
        this.userId = userId;
        this.couponId = couponId;
        this.userCouponStatus = userCouponStatus;
        this.claimTime = claimTime;
    }
}

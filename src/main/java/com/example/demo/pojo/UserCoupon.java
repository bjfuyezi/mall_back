package com.example.demo.pojo;

import com.example.demo.enums.UserCouponStatus;
import lombok.Data;

@Data
public class UserCoupon {
    private Integer userId;
    private Integer couponId;
    private UserCouponStatus userCouponStatus;
    private Data claimTime;

    public UserCoupon(Integer userId, Integer couponId, UserCouponStatus userCouponStatus, Data claimTime) {
        this.userId = userId;
        this.couponId = couponId;
        this.userCouponStatus = userCouponStatus;
        this.claimTime = claimTime;
    }
}

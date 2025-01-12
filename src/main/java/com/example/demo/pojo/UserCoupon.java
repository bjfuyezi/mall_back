package com.example.demo.pojo;

import com.example.demo.enums.UserCouponStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class UserCoupon {
    private Integer user_coupon_id; // 用户优惠券记录ID
    private Integer user_id;        // 用户ID
    private Integer coupon_id;      // 优惠券ID
    private UserCouponStatus status; // 用户优惠券状态
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date claim_time;        // 领取时间

    // 新增  属性
//    private Integer shop_id;             // 店铺ID


    public UserCoupon() {
    }

    public UserCoupon(Integer user_id, Integer coupon_id, UserCouponStatus status, Date claim_time) {
        this.user_id = user_id;
        this.coupon_id = coupon_id;
        this.status = status;
        this.claim_time = claim_time;
    }

//    public UserCoupon(Integer user_coupon_id, Integer user_id, Integer coupon_id, UserCouponStatus status, Date claim_time) {
//        this.user_coupon_id = user_coupon_id;
//        this.user_id = user_id;
//        this.coupon_id = coupon_id;
//        this.status = status;
//        this.claim_time = claim_time;
//    }
}

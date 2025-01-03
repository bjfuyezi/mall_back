package com.example.demo.pojo;

import com.example.demo.enums.CouponStatus;
import com.example.demo.enums.CouponType;
import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
public class Coupon {
    private Integer couponId;
    private CouponType couponType;//优惠券类型
    private Date startTime;
    private Date endTime;
    private String scope; // JSON 格式
    private double request; // 最低消费
    private double off; // 折扣金额
    private CouponStatus couponStatus;//优惠券状态
    private BigInteger total;//总数量
    private Integer claimLimit;//限制领取数量
    private Integer maxUnusedCount;//最多允许账户里存在几张未使用的本券
    private Date createTime;//券的创建时间，根据这个进行排序
    private int shopId;

    public Coupon() {
    }

    public Coupon(CouponType couponType, Date startTime, Date endTime, String scope, double request, double off, CouponStatus couponStatus, BigInteger total, Integer claimLimit, Integer maxUnusedCount, Date createTime, int shopId) {
        this.couponType = couponType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.scope = scope;
        this.request = request;
        this.off = off;
        this.couponStatus = couponStatus;
        this.total = total;
        this.claimLimit = claimLimit;
        this.maxUnusedCount = maxUnusedCount;
        this.createTime = createTime;
        this.shopId = shopId;
    }
}

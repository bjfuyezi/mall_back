package com.example.demo.pojo;

import com.example.demo.enums.CouponStatus;
import com.example.demo.enums.CouponType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Data
public class Coupon {
    private Integer coupon_id; // 优惠券ID
    private CouponType coupon_type; // 优惠券类型
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date start_time; // 开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date end_time; // 结束时间
    private String scope; // 使用范围
    private double request; // 最低消费
    private double off; // 折扣金额
    private CouponStatus coupon_status; // 优惠券状态
    private BigInteger total; // 总数量
    private Integer claim_limit; // 限制领取数量
    private Integer max_unused_count; // 最多允许账户里存在几张未使用的本券
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date create_time; // 创建时间
    private Integer shop_id; // 店铺ID

    public Coupon() {
    }

    public Coupon(CouponType coupon_type, Date start_time, Date end_time, String scope, double request, double off, BigInteger total, Integer claim_limit, Integer max_unused_count ,Integer shop_id) {
        this.coupon_type = coupon_type;
        this.start_time = start_time;
        this.end_time = end_time;
        this.scope = scope;
        this.request = request;
        this.off = off;
        this.total = total;
        this.claim_limit = claim_limit;
        this.max_unused_count = max_unused_count;
        this.shop_id = shop_id;
    }

    public Coupon(CouponType coupon_type, Date start_time, Date end_time, String scope, double request, double off, CouponStatus coupon_status, BigInteger total, Integer claim_limit, Integer max_unused_count, Date create_time, Integer shop_id) {
        this.coupon_type = coupon_type;
        this.start_time = start_time;
        this.end_time = end_time;
        this.scope = scope;
        this.request = request;
        this.off = off;
        this.coupon_status = coupon_status;
        this.total = total;
        this.claim_limit = claim_limit;
        this.max_unused_count = max_unused_count;
        this.create_time = create_time;
        this.shop_id = shop_id;
    }

    public Coupon(CouponType coupon_type, Date start_time, Date end_time, String scope, double request, double off, CouponStatus coupon_status, BigInteger total, Integer claim_limit, Integer max_unused_count,Integer shop_id) {
        this.coupon_type = coupon_type;
        this.start_time = start_time;
        this.end_time = end_time;
        this.scope = scope;
        this.request = request;
        this.off = off;
        this.coupon_status = coupon_status;
        this.total = total;
        this.claim_limit = claim_limit;
        this.max_unused_count = max_unused_count;
        this.create_time = create_time;
        this.shop_id = shop_id;
    }
}

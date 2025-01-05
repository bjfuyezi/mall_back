package com.example.demo.mapper;

import com.example.demo.enums.CouponStatus;
import com.example.demo.enums.CouponType;
import com.example.demo.pojo.Coupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Mapper
public interface CouponMapper {
    void createCoupon(Coupon coupon);

    Coupon selectCouponById(Integer coupon_id);

    int deleteCouponById(Integer coupon_id);

    List<Coupon> selectCouponsByType(CouponType coupon_type);

    List<Coupon> selectCouponsByShop_id(Integer shop_id);

    // 获取当前生效的平台券
    List<Coupon> getActivePlatformCoupons(CouponType coupon_type);

    // 获取某一店铺当前生效的优惠券
    List<Coupon> getActiveShopCoupons(Integer shop_id);

    int updatePendingCouponContent(Integer coupon_id, Date start_time, Date end_time, Double request, Double off,
                                   BigInteger total, Integer claim_limit, Integer max_unused_count);


    int updateActiveCouponContent(Integer coupon_id, Integer total, Integer claim_limit, Integer max_unused_count);

    /**
     * 暂停发放已生效的券
     * @param coupon_id 优惠券ID
     * @return 更新结果
     */
    int pauseActiveCoupon(Integer coupon_id);

    // 更新券的范围
    void updateCouponScope(Coupon coupon);

    int decrementCouponTotal(@Param("coupon_id") Integer coupon_id);
    // @Param 注解，MyBatis 会将参数封装为 Map，自动通过别名查找参数。
}

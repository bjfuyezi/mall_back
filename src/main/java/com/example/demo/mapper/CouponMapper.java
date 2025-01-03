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
    int createCoupon(Coupon coupon);

    Coupon selectCouponById(Integer couponId);

    int deleteCouponById(Integer couponId);

    List<Coupon> selectCouponsByType(CouponType couponType);

    List<Coupon> selectCouponsByShopId(Integer shopId);

    // 获取当前生效的平台券
    List<Coupon> getActivePlatformCoupons(CouponType couponType);

    // 获取某一店铺当前生效的优惠券
    List<Coupon> getActiveShopCoupons(Integer shopId);

    int updatePendingCouponContent(Integer couponId, Date startTime, Date endTime, Double request, Double off,
                                   BigInteger total, Integer claimLimit, Integer maxUnusedCount);


    int updateActiveCouponContent(Integer couponId, Integer total, Integer claimLimit, Integer maxUnusedCount);

    /**
     * 暂停发放已生效的券
     * @param couponId 优惠券ID
     * @return 更新结果
     */
    int pauseActiveCoupon(Integer couponId);

    // 更新券的范围
    void updateCouponScope(Coupon coupon);

    int decrementCouponTotal(@Param("couponId") Integer couponId);
    // @Param 注解，MyBatis 会将参数封装为 Map，自动通过别名查找参数。
}

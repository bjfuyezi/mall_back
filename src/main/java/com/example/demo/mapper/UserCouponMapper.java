package com.example.demo.mapper;

import com.example.demo.pojo.Product;
import com.example.demo.pojo.Shop;
import com.example.demo.pojo.UserCoupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserCouponMapper {
    Integer countClaimedCoupons(Integer coupon_id);

    int countClaimedCouponsByUserId(@Param("user_id") Integer user_id, @Param("coupon_id") Integer coupon_id);
    int countUnusedCouponsByUserId(@Param("user_id") Integer user_id, @Param("coupon_id") Integer coupon_id);
    int insertUserCoupon(UserCoupon userCoupon);

    List<UserCoupon> getPlatformCoupons(@Param("user_id") Integer user_id);

    List<UserCoupon> getShopCoupons(@Param("user_id") Integer user_id);

    // 查询用户的符合状态的平台券
    List<UserCoupon> getPlatformCouponsByStatus(@Param("user_id") Integer user_id,
                                                @Param("status") String status);

    // 查询用户的符合状态的店铺券
    List<UserCoupon> getShopCouponsByStatus(@Param("user_id") Integer user_id,
                                            @Param("status") String status);

    // 获取优惠券的类型和范围
    Map<String, Object> getCouponTypeAndScope(@Param("coupon_id") Integer coupon_id);

    // 更新优惠券状态为 Used
    int updateCouponStatusToUsed(@Param("user_id") Integer user_id, @Param("coupon_id") Integer coupon_id);

    //其实本来应该是product和shop类的,[现在懒得管了]
    // 根据商品 ID 查询商品详情
    List<Product> getProductsByIds(@Param("ids") List<Integer> ids);

    // 根据店铺 ID 查询店铺详情
    List<Shop> getShopsByIds(@Param("ids") List<Integer> ids);

}

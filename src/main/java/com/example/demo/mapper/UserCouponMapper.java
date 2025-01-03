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
    Integer countClaimedCoupons(Integer couponId);

    int countClaimedCouponsByUserId(@Param("userId") Integer userId, @Param("couponId") Integer couponId);
    int countUnusedCouponsByUserId(@Param("userId") Integer userId, @Param("couponId") Integer couponId);
    int insertUserCoupon(UserCoupon userCoupon);

    List<UserCoupon> getPlatformCoupons(@Param("userId") Integer userId);

    List<UserCoupon> getShopCoupons(@Param("userId") Integer userId);

    // 查询用户的符合状态的平台券
    List<UserCoupon> getPlatformCouponsByStatus(@Param("userId") Integer userId,
                                                @Param("status") String status);

    // 查询用户的符合状态的店铺券
    List<UserCoupon> getShopCouponsByStatus(@Param("userId") Integer userId,
                                            @Param("status") String status);

    // 获取优惠券的类型和范围
    Map<String, Object> getCouponTypeAndScope(@Param("couponId") Integer couponId);

    // 更新优惠券状态为 Used
    int updateCouponStatusToUsed(@Param("userId") Integer userId, @Param("couponId") Integer couponId);

    //其实本来应该是product和shop类的,[现在懒得管了]
    // 根据商品 ID 查询商品详情
    List<Product> getProductsByIds(@Param("ids") List<Integer> ids);

    // 根据店铺 ID 查询店铺详情
    List<Shop> getShopsByIds(@Param("ids") List<Integer> ids);

}

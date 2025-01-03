package com.example.demo.service;

import com.example.demo.enums.UserCouponStatus;
import com.example.demo.mapper.CouponMapper;
import com.example.demo.mapper.UserCouponMapper;
import com.example.demo.pojo.Coupon;
import com.example.demo.pojo.Product;
import com.example.demo.pojo.Shop;
import com.example.demo.pojo.UserCoupon;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserCouponService {
    @Autowired
    private UserCouponMapper userCouponMapper;
    @Autowired
    private CouponMapper couponMapper;
    // Jackson 对象，用于解析 JSON
    private final ObjectMapper objectMapper = new ObjectMapper();
    /*用户领券【增】
     * 传入参数：
     *   - 用户id：userId
     *   - 券的id：couponId
     * 处理结果：
     *   - 先进行判断：如果以下三类判断有不符合的，则不创建记录，给出提示
     *       - 是否券数量大于0
     *       - 用户未使用的券是否超过限制
     *       - 用户是否已达到账户存放上限
     *   - 最后：以用户id、券的id、默认初始状态Unused、领取时间为当前时间为参数创建一条记录 */
    public String claimCoupon(Integer userId, Integer couponId) {
        // 获取优惠券信息
        Coupon coupon = couponMapper.selectCouponById(couponId);
        if (coupon == null) {
            return "优惠券不存在";
        }
        // 判断券的库存
        if (coupon.getTotal().compareTo(BigInteger.ZERO) <= 0) {
            return "优惠券已被领完";
        }

        // 判断用户未使用的券是否超过限制
        int unusedCount = userCouponMapper.countUnusedCouponsByUserId(userId, couponId);
        if (unusedCount >= coupon.getMaxUnusedCount()) {
            return "未使用的同类优惠券已达到上限";
        }

        // 判断用户领券数量是否已达到账户存放上限
        int userClaimedCount = userCouponMapper.countClaimedCouponsByUserId(userId, couponId);
        if (userClaimedCount >= coupon.getClaimLimit()) {
            return "您已达本优惠券的领取上限";
        }

        // 插入用户领券记录
        UserCoupon userCoupon = new UserCoupon(userId, couponId, UserCouponStatus.Unused, new Date());
        int result = userCouponMapper.insertUserCoupon(userCoupon);

        // 更新优惠券库存
        if (result > 0) {
            couponMapper.decrementCouponTotal(couponId);
            return "优惠券领取成功";
        } else {
            return "优惠券领取失败";
        }
    }


    /*用户查看券列表（全部）【查】
     * 列表展示有一定的要求，要求如下：
     *   - 优先展示所有平台券，平台券按照领取时间，晚领取的优先展示
     *   - 展示完所有平台券后，根据店铺进行分块展示，展示具体要求如下：
     *       - 每个店铺下的券按照领取时间排序，晚领取的优先展示
     *       - 店铺分块展示的顺序由每个店铺中最晚领取的券的时间决定，这个时间越晚，排序越靠前
     *   - 这里不区分券的状态，全部展示
     * /

     */
     /* 最后返回的数据形式：
     * {
            "platformCoupons": [...],
            "shopCoupons": [
                {
                    "shopId": 1,
                    "coupons": [...]
                },
                {
                    "shopId": 2,
                    "coupons": [...]
                }
            ]
        }
        * 其中，券是按照领取时间先后顺序排列，晚领取的先排列，
        * 且每个店铺第一张券的领取时间一定晚于上一个店铺的第一张券的领取时间，
        * 一定早于下一个店铺的第一张券的领取时间*/
    public Map<String, Object> getUserCoupons(Integer userId) {
        // 查询所有平台券，按领取时间降序排列
        List<UserCoupon> platformCoupons = userCouponMapper.getPlatformCoupons(userId);

        // 查询所有店铺券
        List<UserCoupon> shopCoupons = userCouponMapper.getShopCoupons(userId);
        // 按店铺的 couponId 对店铺券进行分组
        /*shopCouponsGrouped 是一个 Map<Integer, List<UserCoupon>> 类型，
        键为店铺 ID (shopId)，值为该店铺的所有优惠券 (List<UserCoupon>)*/
        Map<Integer, List<UserCoupon>> shopCouponsGrouped = groupCouponsByShop(shopCoupons);

        // 对店铺券分组按照每组的最近领取时间降序排序
        /*  entrySet() 方法将 Map 转换为包含键值对的 Set<Map.Entry<Integer, List<UserCoupon>>>，
            然后通过 stream() 将其转换为流，以便应用流操作
            sorted 方法对流中的元素进行排序。排序规则由 Comparator 指定，
            这里通过一个 lambda 表达式 (e1, e2) -> {...} 定义排序逻辑。
            e1.getValue(): 获取第一个店铺的优惠券列表 (List<UserCoupon>）。
            .get(0): 取出列表中第一个对象。
            .getClaimTime(): 获取领取时间
        */
        List<Map.Entry<Integer, List<UserCoupon>>> sortedShops = shopCouponsGrouped.entrySet().stream()
                .sorted((e1, e2) -> {
                    // 获取每个分组中最近领取的券的时间
                    Date latest1 = e1.getValue().get(0).getClaimTime();
                    Date latest2 = e2.getValue().get(0).getClaimTime();
                    return latest2.compareTo(latest1); // 按时间降序排序
                })
                .collect(Collectors.toList());
                //将排序后的流收集为一个 List<Map.Entry<Integer, List<UserCoupon>>>。
                // 每个元素是一个店铺 ID 和对应的优惠券列表。

        // 构造返回结果
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("platformCoupons", platformCoupons); // 平台券
        result.put("shopCoupons", sortedShops); // 店铺券分组

        return result;
    }
    // 私有方法：根据店铺ID分组店铺券
    private Map<Integer, List<UserCoupon>> groupCouponsByShop(List<UserCoupon> shopCoupons) {
        Map<Integer, List<UserCoupon>> groupedCoupons = new HashMap<>();
        for (UserCoupon userCoupon : shopCoupons) {
            // 获取当前券的ID
            Integer couponId = userCoupon.getCouponId();
            // 通过券ID获取券信息，获得店铺ID
            Coupon coupon = couponMapper.selectCouponById(couponId);
            Integer shopId = coupon.getShopId();

            // 如果map中不存在当前店铺ID，创建一个新的列表
            groupedCoupons.putIfAbsent(shopId, new ArrayList<>());
            // 将当前用户券添加到对应店铺ID的列表中
            groupedCoupons.get(shopId).add(userCoupon);
        }
        return groupedCoupons;
    }

    /*用户查看券列表（根据状态分类）【查】
     * 列表展示有一定的要求，要求如下：
     *   - 优先展示所有平台券，平台券按照领取时间，晚领取的优先展示
     *   - 展示完所有平台券后，根据店铺进行分块展示，展示具体要求如下：
     *       - 每个店铺下的券按照领取时间排序，晚领取的优先展示
     *       - 店铺分块展示的顺序由每个店铺中最晚领取的券的时间决定，这个时间越晚，排序越靠前
     *   - 这里区分券的状态，需要传入券的某一状态，将这一状态的券按照上述要求返回给前端*/
    public Map<String, Object> getUserCouponsByStatus(Integer userId, String status) {
        // 查询所有符合状态的用户平台券
        List<UserCoupon> platformCoupons = userCouponMapper.getPlatformCouponsByStatus(userId, status);

        // 查询所有符合状态的用户店铺券
        List<UserCoupon> shopCoupons = userCouponMapper.getShopCouponsByStatus(userId, status);

        // 按店铺分组
        Map<Integer, List<UserCoupon>> shopCouponsGrouped = groupCouponsByShop(shopCoupons);

        // 按每个店铺的最近领取时间排序
        List<Map.Entry<Integer, List<UserCoupon>>> sortedShops = shopCouponsGrouped.entrySet().stream()
                .sorted((e1, e2) -> {
                    Date latest1 = e1.getValue().get(0).getClaimTime(); // 券列表按领取时间降序，第一个为最晚
                    Date latest2 = e2.getValue().get(0).getClaimTime();
                    return latest2.compareTo(latest1);
                })
                .collect(Collectors.toList());

        // 构造返回结果
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("platformCoupons", platformCoupons);
        result.put("shopCoupons", sortedShops);

        return result;
    }


    /*用户查看券的适用店铺或者适用商品【查】
     * 传入参数：
     *   - 券的id:couponId
     * 返回值：
     *  - 店铺列表或者是商品列表*/
    public Map<String, List<Object>> getCouponScopeDetails(Integer couponId) {
        Map<String, List<Object>> result = new HashMap<>();

        // 获取优惠券的类型和范围 JSON
        Map<String, Object> couponInfo = userCouponMapper.getCouponTypeAndScope(couponId);
        if (couponInfo == null) {
            throw new IllegalArgumentException("无效的优惠券ID：" + couponId);
        }

        String couponType = (String) couponInfo.get("couponType");
        String scopeJson = (String) couponInfo.get("scope");

        try {
            // 解析范围 JSON 为 ID 列表
            List<Integer> ids = objectMapper.readValue(scopeJson, List.class);

            if ("shop".equalsIgnoreCase(couponType)) {
                // 查询商品详情
                List<Product> products = userCouponMapper.getProductsByIds(ids);
                result.put("shop", new ArrayList<>(products));
            } else if ("platform".equalsIgnoreCase(couponType)) {
                // 查询店铺详情
                List<Shop> shops = userCouponMapper.getShopsByIds(ids);
                result.put("platform", new ArrayList<>(shops));
            } else {
                throw new IllegalArgumentException("未知的优惠券类型：" + couponType);
            }

        } catch (Exception e) {
            throw new RuntimeException("解析范围 JSON 时出错", e);
        }

        return result;
    }

    /*用户使用券，券的类型进行更改为Used【改】
     * 传入参数：
     *   - 用户id:userId
     *   - 券的id:couponId*/
    public boolean useCoupon(Integer userId, Integer couponId) {
        int affectedRows = userCouponMapper.updateCouponStatusToUsed(userId, couponId);
        return affectedRows > 0;
    }

    /*券达到失效时间，更改状态为已失效Expired【改】【自动-定时】TODO
     * 传入参数：
     *   - 用户id:userId
     *   - 券的id:couponId*/
}

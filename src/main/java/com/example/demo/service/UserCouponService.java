package com.example.demo.service;

import com.example.demo.enums.CouponStatus;
import com.example.demo.enums.UserCouponStatus;
import com.example.demo.mapper.CouponMapper;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.mapper.ShopMapper;
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
    @Autowired
    private ShopMapper shopMapper;
    @Autowired
    private ProductMapper productMapper;
    // Jackson 对象，用于解析 JSON
    private final ObjectMapper objectMapper = new ObjectMapper();
    /*用户领券【增】
     * 传入参数：
     *   - 用户id：user_id
     *   - 券的id：coupon_id
     * 处理结果：
     *   - 先进行判断：如果以下三类判断有不符合的，则不创建记录，给出提示
     *       - 是否券数量大于0
     *       - 用户未使用的券是否超过限制
     *       - 用户是否已达到账户存放上限
     *   - 最后：以用户id、券的id、默认初始状态Unused、领取时间为当前时间为参数创建一条记录 */
    // 测试合格--浮笙
    public String claimCoupon(Integer user_id, Integer coupon_id) {
        // 获取优惠券信息
        Coupon coupon = couponMapper.selectCouponById(coupon_id);
        if (coupon == null) {
            return "优惠券不存在";
        }
        //更新当前券的状态，给出id
        CouponStatus before = coupon.getCoupon_status();
        int updateNum = couponMapper.updateCouponStatusByNowTime(coupon_id);
        coupon = couponMapper.selectCouponById(coupon_id);
        if(updateNum>0){
            System.out.println(coupon);
            System.out.println("优惠券"+coupon_id+"的状态从"+before+"变成了"+coupon.getCoupon_status());
        }
        if(coupon.getCoupon_status()!=CouponStatus.Active){
            return "优惠券状态异常，无法领取";
        }
        // 判断券的库存
        if (coupon.getTotal().compareTo(BigInteger.ZERO) <= 0) {
            return "优惠券已被领完";
        }
        // 判断用户未使用的券是否已达到账户存放上限
        int unusedCount = userCouponMapper.countUnusedCouponsByUser_id(user_id, coupon_id);
        if (unusedCount >= coupon.getMax_unused_count()) {
            return "未使用的同类优惠券已达到上限";
        }
        // 判断用户领券数量是否超过限制
        int userClaimedCount = userCouponMapper.countClaimedCouponsByUser_id(user_id, coupon_id);
        if (userClaimedCount >= coupon.getClaim_limit()) {
            return "您已达本优惠券的领取上限";
        }
        // 插入用户领券记录
        UserCoupon userCoupon = new UserCoupon(user_id, coupon_id, UserCouponStatus.Unused, new Date());
        System.out.println(userCoupon);
        int result = userCouponMapper.insertUserCoupon(userCoupon);

        // 更新优惠券库存
        if (result > 0) {
            couponMapper.decrementCouponTotal(coupon_id);//券的库存减一
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
     *
     */
     /* 最后返回的数据形式：
     * {
            "platformCoupons": [...],
            "shopCoupons": [
                {
                    "shop_id": 1,
                    "shop_name": 1,
                    "coupons": [...]
                },
                {
                    "shop_id": 2,
                    "shop_name": 2,
                    "coupons": [...]
                }
            ]
        }
        * 其中，券是按照领取时间先后顺序排列，晚领取的先排列，
        * 且每个店铺第一张券的领取时间一定晚于上一个店铺的第一张券的领取时间，
        * 一定早于下一个店铺的第一张券的领取时间*/
    // 测试合格--浮笙
    public Map<String, Object> getUserCoupons(Integer user_id) {
        System.out.println(user_id);
        //更新用户券
        List<UserCoupon> all = userCouponMapper.getALLByUser_id(user_id);
        updateExpiredCoupons(all);

        // 查询所有平台券，按领取时间降序排列
        List<UserCoupon> platformCoupons = userCouponMapper.getPlatformCoupons(user_id);

        // 查询所有店铺券
        List<UserCoupon> shopCoupons = userCouponMapper.getShopCoupons(user_id);
        // 按店铺的 coupon_id 对店铺券进行分组
        /*shopCouponsGrouped 是一个 Map<Integer, List<UserCoupon>> 类型，
        键为店铺 ID ()，值为该店铺的所有优惠券 (List<UserCoupon>)*/
//        Map<Integer, List<UserCoupon>> shopCouponsGrouped = groupCouponsByShop(shopCoupons);

        // 对店铺券分组按照每组的最近领取时间降序排序
        /*  entrySet() 方法将 Map 转换为包含键值对的 Set<Map.Entry<Integer, List<UserCoupon>>>，
            然后通过 stream() 将其转换为流，以便应用流操作
            sorted 方法对流中的元素进行排序。排序规则由 Comparator 指定，
            这里通过一个 lambda 表达式 (e1, e2) -> {...} 定义排序逻辑。
            e1.getValue(): 获取第一个店铺的优惠券列表 (List<UserCoupon>）。
            .get(0): 取出列表中第一个对象。
            .getClaimTime(): 获取领取时间
        */
//        List<Map.Entry<Integer, List<UserCoupon>>> sortedShops = shopCouponsGrouped.entrySet().stream()
//                .sorted((e1, e2) -> {
//                    // 获取每个分组中最近领取的券的时间
//                    Date latest1 = e1.getValue().get(0).getClaim_time();
//                    Date latest2 = e2.getValue().get(0).getClaim_time();
//                    return latest2.compareTo(latest1); // 按时间降序排序
//                })
//                .collect(Collectors.toList());
//                //将排序后的流收集为一个 List<Map.Entry<Integer, List<UserCoupon>>>。
//                // 每个元素是一个店铺 ID 和对应的优惠券列表。

        // 对店铺券分组并处理店铺信息（包括 shop_id、shop_name 和对应的优惠券列表）
        List<Map<String, Object>> sortedShops = groupCouponsByShop(shopCoupons);

        // 构造返回结果
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("platformCoupons", platformCoupons); // 平台券
        result.put("shopCoupons", sortedShops); // 店铺券分组

        return result;
    }
    // 私有方法：根据店铺ID分组店铺券，并包含 shop_name 信息
    private List<Map<String, Object>> groupCouponsByShop(List<UserCoupon> shopCoupons) {
        // 按店铺ID分组优惠券
        Map<Integer, List<UserCoupon>> groupedCoupons = new HashMap<>();
        for (UserCoupon userCoupon : shopCoupons) {
            // 获取当前券的ID
            Integer coupon_id = userCoupon.getCoupon_id();
            // 通过券ID获取券信息，获得店铺ID
            Coupon coupon = couponMapper.selectCouponById(coupon_id);
            Integer shop_id = coupon.getShop_id();

            // 如果 map 中不存在当前店铺ID，创建一个新的列表
            groupedCoupons.putIfAbsent(shop_id, new ArrayList<>());
            // 将当前用户券添加到对应店铺ID的列表中
            groupedCoupons.get(shop_id).add(userCoupon);
        }

        // 构造包含 shop_name 的返回格式，并对分组按照最近领取时间降序排序
        return groupedCoupons.entrySet().stream()
                .sorted((e1, e2) -> {
                    // 获取每个分组中最近领取的券的时间
                    Date latest1 = e1.getValue().get(0).getClaim_time();
                    Date latest2 = e2.getValue().get(0).getClaim_time();
                    return latest2.compareTo(latest1); // 按时间降序排序
                })
                .map(entry -> {
                    Integer shop_id = entry.getKey();
                    List<UserCoupon> coupons = entry.getValue();
                    // 查询店铺名称
                    Shop shop = shopMapper.selectById(shop_id);
                    String shop_name = shop.getShop_name();

                    // 构造每个店铺的返回 Map
                    Map<String, Object> shopInfo = new LinkedHashMap<>();
                    shopInfo.put("shop_id", shop_id);
                    shopInfo.put("shop_name", shop_name);
                    shopInfo.put("coupons", coupons);
                    return shopInfo;
                })
                .collect(Collectors.toList()); // 将结果收集为 List<Map<String, Object>>
    }

//    private Map<Integer, List<UserCoupon>> groupCouponsByShop2(List<UserCoupon> shopCoupons) {
//        // 使用一个临时 Map 以 shop_id 为键分组
//        Map<Integer, List<UserCoupon>> groupedCoupons = new HashMap<>();
//        Map<Integer, String> shopNames = new HashMap<>(); // 存储店铺名称
//        for (UserCoupon userCoupon : shopCoupons) {
//            // 获取当前券的ID
//            Integer coupon_id = userCoupon.getCoupon_id();
//            // 通过券ID获取券信息，获得店铺ID
//            Coupon coupon = couponMapper.selectCouponById(coupon_id);
//            Integer shop_id = coupon.getShop_id();
//            Shop shop = shopMapper.selectById(shop_id);
//            System.out.println("shop_id:"+shop_id);
//            System.out.println("shop:"+shop);
//            String shop_name = shop.getShop_name();
//
//            // 如果map中不存在当前店铺ID，创建一个新的列表
//            groupedCoupons.putIfAbsent(shop_id, new ArrayList<>());
//            // 将当前用户券添加到对应店铺ID的列表中
//            groupedCoupons.get(shop_id).add(userCoupon);
//        }
//        return groupedCoupons;
//    }

    /*用户查看券列表（根据状态分类）【查】
     * 列表展示有一定的要求，要求如下：
     *   - 优先展示所有平台券，平台券按照领取时间，晚领取的优先展示
     *   - 展示完所有平台券后，根据店铺进行分块展示，展示具体要求如下：
     *       - 每个店铺下的券按照领取时间排序，晚领取的优先展示
     *       - 店铺分块展示的顺序由每个店铺中最晚领取的券的时间决定，这个时间越晚，排序越靠前
     *   - 这里区分券的状态，需要传入券的某一状态，将这一状态的券按照上述要求返回给前端*/
    // TODO:查找前先更新下用户的优惠券状态（主要是为了未使用的券变成了过期），
    //  通过输入id查找用户拥有的所有的特定品类和状态的券（这里会得到一个List<UserCoupon>），
    //  然后通过对这个列表进行处理，获取列表中的每一个项以及对应的coupon_id，再通过该id查找券，如有需要更新券的状态为Expired，没有则不变，
    //  再然后如果该券的状态为Expired,那么用户手中的券的状态也要随之改变，但是要注意的是：只有Unused的券会过期Expired，
    //  使用过Used的券不会变【私有函数updateExpiredCoupons完成】
    // 测试合格--浮笙
    public Map<String, Object> getUserCouponsByStatus(Integer user_id, String status) {
        //更新用户券
        List<UserCoupon> all = userCouponMapper.getALLByUser_id(user_id);
        updateExpiredCoupons(all);

        // 查询所有符合状态的用户平台券
        List<UserCoupon> platformCoupons = userCouponMapper.getPlatformCouponsByStatus(user_id, status);

        // 查询所有符合状态的用户店铺券
        List<UserCoupon> shopCoupons = userCouponMapper.getShopCouponsByStatus(user_id, status);

        // 按店铺分组
//        Map<Integer, List<UserCoupon>> shopCouponsGrouped = groupCouponsByShop2(shopCoupons);
//
//        // 按每个店铺的最近领取时间排序
//        List<Map.Entry<Integer, List<UserCoupon>>> sortedShops = shopCouponsGrouped.entrySet().stream()
//                .sorted((e1, e2) -> {
//                    Date latest1 = e1.getValue().get(0).getClaim_time(); // 券列表按领取时间降序，第一个为最晚
//                    Date latest2 = e2.getValue().get(0).getClaim_time();
//                    return latest2.compareTo(latest1);
//                })
//                .collect(Collectors.toList());
        // 对店铺券分组并处理店铺信息（包括 shop_id、shop_name 和对应的优惠券列表）
        List<Map<String, Object>> sortedShops = groupCouponsByShop(shopCoupons);

        // 构造返回结果
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("platformCoupons", platformCoupons);
        result.put("shopCoupons", sortedShops);

        return result;
    }


    /*用户查看券的适用店铺或者适用商品【查】
     * 传入参数：
     *   - 券的id:coupon_id
     * 返回值：
     *  - 店铺列表或者是商品列表*/
    public Map<String, List<Object>> getCouponScopeDetails(Integer coupon_id) {
        Map<String, List<Object>> result = new HashMap<>();

        // 获取优惠券的类型和范围 JSON
        Map<String, Object> couponInfo = userCouponMapper.getCouponTypeAndScope(coupon_id);
        if (couponInfo == null) {
            throw new IllegalArgumentException("无效的优惠券ID：" + coupon_id);
        }

        String coupon_type = (String) couponInfo.get("coupon_type");
        String scopeJson = (String) couponInfo.get("scope");
        System.out.println(coupon_type);
        System.out.println(scopeJson);
        try {
            // 解析范围 JSON 为 ID 列表
            List<Integer> ids = objectMapper.readValue(scopeJson, List.class);
            System.out.println("ids:"+ids);

            if ("shop".equalsIgnoreCase(coupon_type)) {
                // 查询商品详情
                List<Product> products = new ArrayList<>();
                for(Integer id:ids){
                    System.out.println(id);
                    Product product= productMapper.selectById(id);
                    System.out.println(product);
                    products.add(product);
                }
//                List<Product> products = userCouponMapper.getProductsByIds(ids);
                result.put("shop", new ArrayList<>(products));
            } else if ("platform".equalsIgnoreCase(coupon_type)) {
                // 查询店铺详情
//                List<Shop> shops = userCouponMapper.getShopsByIds(ids);
                List<Shop> shops = new ArrayList<>();
                for(Integer id:ids){
                    System.out.println(id);
                    Shop shop = shopMapper.selectById(id);
                    System.out.println(shop);
                    shops.add(shop);
                }
                result.put("platform", new ArrayList<>(shops));
            } else {
                throw new IllegalArgumentException("未知的优惠券类型：" + coupon_type);
            }

        } catch (Exception e) {
            throw new RuntimeException("解析范围 JSON 时出错", e);
        }

        return result;
    }

    /*用户使用券，券的类型进行更改为Used【改】
     * 传入参数：
     *   - 用户券id:user_coupon_id*/
    // 测试合格--浮笙
    public boolean useCoupon(Integer user_coupon_id) {
        updateUserCoupon(user_coupon_id);
        UserCoupon userCoupon = userCouponMapper.getUserCouponById(user_coupon_id);
        if(userCoupon.getStatus()==UserCouponStatus.Expired){
            throw new RuntimeException("优惠券已经过期");
        }else if(userCoupon.getStatus()==UserCouponStatus.Used){
            throw new RuntimeException("优惠券已经使用");
        }
        int affectedRows = userCouponMapper.updateCouponStatusToUsed(user_coupon_id);
        return affectedRows > 0;
    }

    /*券达到失效时间，更改状态为已失效Expired【改】【自动-定时】TODO
     * 不要自动了，改成在用户查看券、选择券等时候先更新一下状态，看是否失效
     * */
    // 私有函数：更新用户优惠券的状态【测试合格--浮笙】
    private void updateExpiredCoupons(List<UserCoupon> userCoupons) {
        System.out.println("----------更新开始----------");
        for (UserCoupon userCoupon : userCoupons) {
            int coupon_id = userCoupon.getCoupon_id();
            Coupon coupon = couponMapper.selectCouponById(coupon_id);//获取得到初始的券信息
            if(coupon!=null){
                CouponStatus before = coupon.getCoupon_status();
                int updateNum = couponMapper.updateCouponStatusByNowTime(coupon_id);
                coupon = couponMapper.selectCouponById(coupon_id);
                if(updateNum>0){
                    System.out.println(coupon);
                    System.out.println("优惠券"+coupon_id+"的状态从"+before+"变成了"+coupon.getCoupon_status());
                }
                if(userCoupon.getStatus()==UserCouponStatus.Unused && coupon.getCoupon_status()==CouponStatus.Expired){
                    UserCouponStatus before2 = userCoupon.getStatus();
                    //如果该券的状态为Expired,那么用户手中的券的状态也要随之改变，但是要注意的是：只有Unused的券会过期Expired，使用过Used的券不会变
                    int result = userCouponMapper.updateUserCouponStatusToExpired(userCoupon.getUser_coupon_id());
                    UserCoupon userCoupon2 = userCouponMapper.getUserCouponById(userCoupon.getUser_coupon_id());
                    if(result>0){
                        System.out.println(userCoupon2);
                        System.out.println("用户优惠券"+userCoupon.getUser_coupon_id()+"的状态从"+before2+"变成了"+userCoupon2.getStatus());
                    }
                }
            }
        }
        System.out.println("----------更新完成----------");
    }

    private void updateUserCoupon(Integer user_coupon_id){
        UserCoupon userCoupon = userCouponMapper.getUserCouponById(user_coupon_id);
        int coupon_id = userCoupon.getCoupon_id();
        Coupon coupon = couponMapper.selectCouponById(coupon_id);//获取得到初始的券信息
        if(coupon!=null){
            CouponStatus before = coupon.getCoupon_status();
            int updateNum = couponMapper.updateCouponStatusByNowTime(coupon_id);
            coupon = couponMapper.selectCouponById(coupon_id);
            if(updateNum>0){
                System.out.println(coupon);
                System.out.println("优惠券"+coupon_id+"的状态从"+before+"变成了"+coupon.getCoupon_status());
            }
            if(userCoupon.getStatus()==UserCouponStatus.Unused && coupon.getCoupon_status()==CouponStatus.Expired){
                UserCouponStatus before2 = userCoupon.getStatus();
                //如果该券的状态为Expired,那么用户手中的券的状态也要随之改变，但是要注意的是：只有Unused的券会过期Expired，使用过Used的券不会变
                int result = userCouponMapper.updateUserCouponStatusToExpired(userCoupon.getUser_coupon_id());
                UserCoupon userCoupon2 = userCouponMapper.getUserCouponById(userCoupon.getUser_coupon_id());
                if(result>0){
                    System.out.println(userCoupon2);
                    System.out.println("用户优惠券"+userCoupon.getUser_coupon_id()+"的状态从"+before2+"变成了"+userCoupon2.getStatus());
                }
            }
        }
    }
}

package com.example.demo.service;

import com.example.demo.config.Utils;
import com.example.demo.enums.CouponType;
import com.example.demo.mapper.CouponMapper;
import com.example.demo.mapper.UserCouponMapper;
import com.example.demo.pojo.Coupon;
import com.example.demo.enums.CouponStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Service
public class CouponService {
    @Autowired
    private CouponMapper couponMapper;
    @Autowired
    private UserCouponMapper userCouponMapper;
    /*优惠券的创建：【增】
     * 前端需要传入的数据有：
     *   -类型couponType:根据前端不同身份用户创建时指定，卖家指定为shop,平台指定为platform
     *   -开始时间startTime:表单传参
     *   -结束时间endTime:表单传参
     *   -适用范围scope：平台券初始默认为空，店铺券必须初始至少支持适用一个商品，后续在生效前都可以再添加或者修改
     *   -最低消费request:表单传参
     *   -满减金额off:表单传参
     *   -状态couponStatus:创建时初始默认为Pending【待生效】，不用传入
     *   -总数量total：表单传参，表单上有默认值99999999
     *   -限制领取数量claimLimit：表单传参，表单上有默认值3
     *   -单用户账户内未使用的此优惠券最大数量maxUnusedCount：表单传参，表单上有默认值3
     *   -店铺id：shopId,如果是平台券则为0
     * */
    public void createCoupon(
            CouponType couponType, String startTime, String endTime,
            String scope, double request, double off,
            BigInteger total, Integer claimLimit, Integer maxUnusedCount,
            Integer shopId
    ) throws Exception {
        // 使用 Utils.TimetoDate 方法解析日期
        Date start = Utils.TimetoDate(startTime);
        Date end = Utils.TimetoDate(endTime);
        // 先进行检查
        if (request <= 0 || off <= 0) {
            throw new IllegalArgumentException("最低消费或折扣金额必须为正");
        }
        if (total.compareTo(BigInteger.ZERO) <= 0) {
            throw new IllegalArgumentException("总数量必须为正");
        }
        if (claimLimit <= 0 || maxUnusedCount <= 0) {
            throw new IllegalArgumentException("领取限制或未使用限制必须为正");
        }
        if (start.after(end)) {
            throw new IllegalArgumentException("开始时间不能晚于结束时间");
        }
        // 创建 Coupon 对象，直接使用构造函数赋值
        Coupon coupon = new Coupon(couponType, start, end, scope, request, off,
                CouponStatus.Pending, // 初始状态为 Pending
                total, claimLimit, maxUnusedCount, new Date(), // 当前时间作为创建时间
                shopId
        );
        // 调用 Mapper 插入数据库
        int rowsAffected = couponMapper.createCoupon(coupon);
        if (rowsAffected <= 0) {
            throw new Exception("优惠券创建失败");
        }
    }

    /*删除某券【删】
     * 传入参数：
     *   - 券的id：couponId
     * 处理逻辑：
     *   - 先判断，券的状态，如果为Pending未生效，则可以直接删除，如果为其它状态则需要判断券是否被领取过，
     *   - 也就是用户的领取的券里面是否有该券，有则不可以删除并给出提示，没有则可以删除*/
    // 删除优惠券
    public boolean deleteCoupon(Integer couponId) {
        Coupon coupon = couponMapper.selectCouponById(couponId);
        if (coupon == null) {
            throw new IllegalArgumentException("优惠券不存在");
        }
        if (coupon.getCouponStatus() == CouponStatus.Pending) {//待生效，必定没有人领取
            return couponMapper.deleteCouponById(couponId) > 0;
        }
        int claimedCount = userCouponMapper.countClaimedCoupons(couponId);
        if (claimedCount > 0) {
            return false; // 优惠券已被领取，无法删除
        }
        return couponMapper.deleteCouponById(couponId) > 0;
    }

    /*平台管理员页面展示平台券，根据券的创建时间排序
     *   - 创建的晚的券排序更靠前
     *   - 列表形式展示
     *   - 提供的功能有：（修改）、（暂停发放）、（删除）【前端按钮实现】*/
    public List<Coupon> getPlatformCoupons() {
        return couponMapper.selectCouponsByType(CouponType.platform);
    }


    /*店铺管理员页面展示自家店铺券，根据券的创建时间排序
     *   - 创建的晚的券排序更靠前
     *   - 列表形式展示
     *   - 提供的功能有：（修改）、（暂停发放）、（删除）【前端按钮实现】*/
    public List<Coupon> getShopCoupons(Integer shopId) {
        return couponMapper.selectCouponsByShopId(shopId);
    }

    /*用户页面展示平台券【查】
     * 没有传入参数
     * 返回结果：
     *   - 平台当前所有已生效Active状态的平台券*/
    public List<Coupon> getActivePlatformCoupons() {
        return couponMapper.getActivePlatformCoupons(CouponType.platform);
    }


    /*用户页面展示某一店铺券【查】
     * 传入参数：
     *   - 店铺id：shopId
     * 返回结果：
     *   - 该店铺当前所有已生效状态Active的店铺优惠券*/
    public List<Coupon> getActiveShopCoupons(Integer shopId) {
        return couponMapper.getActiveShopCoupons(shopId);
    }

    /*券未生效前，修改券的内容【改】
     * 可以修改：开始时间、结束时间、最低消费、满减金额、总数量、限制领取数量、单用户账户内未使用的此优惠券最大数量
     * 传入券的id和要修改的内容*/
    public void updatePendingCouponContent(Integer couponId, String startTime, String endTime,
                                           Double request, Double off, BigInteger total, Integer claimLimit,
                                           Integer maxUnusedCount) throws Exception {

        Date start = Utils.TimetoDate(startTime);
        Date end =Utils.TimetoDate(endTime);

        // 调用Mapper更新未生效券
        int rowsAffected = couponMapper.updatePendingCouponContent(couponId, start, end, request, off, total, claimLimit, maxUnusedCount);
        if (rowsAffected <= 0) {
            throw new Exception("未生效优惠券修改失败");
        }
    }


    /*券生效后，修改券的内容【改】
     * 可以修改：总数量、限制领取数量、单用户账户内未使用的此优惠券最大数量
     * 传入券的id和要修改的内容*/
    public void updateActiveCouponContent(Integer couponId, Integer total, Integer claimLimit, Integer maxUnusedCount) throws Exception {
        // 调用Mapper更新已生效券
        int rowsAffected = couponMapper.updateActiveCouponContent(couponId, total, claimLimit, maxUnusedCount);
        if (rowsAffected <= 0) {
            throw new Exception("已生效优惠券修改失败");
        }
    }


    /*券生效后，暂停发放券【改】
     * 传入券的id，将该券的状态变更为Paused【暂停领取】【主要是为了避免发错券后的补救措施】*/
    public void pauseActiveCoupon(Integer couponId) throws Exception {
        // 调用Mapper暂停已生效券
        int rowsAffected = couponMapper.pauseActiveCoupon(couponId);
        if (rowsAffected <= 0) {
            throw new Exception("暂停优惠券发放失败");
        }
    }

    /*券生效前，不想发放券了，需要点击一个什么按钮【改/删】（todo:按钮想法
     * 传入券的id,将该券的状态设置为已失效，或者直接删除该券，此时该券必定没有用户领取【这个在删除券的地方已经实现了】*/

    /*店铺加入参与某一平台券【改】
     * 传入参数：
     *   - 店铺id：shopId,前端传入
     *   - 平台券id：couponId,前端传入
     * 结果：
     *   将该id券的适用范围增加一个店铺id信息并更新*/
    public void addShopToCoupon(Integer shopId, Integer couponId) throws Exception {
        Coupon coupon = couponMapper.selectCouponById(couponId);
        if (coupon == null) {
            throw new Exception("优惠券不存在");
        }

        // 处理 coupon scope，将店铺id加入适用范围
        String currentScope = coupon.getScope();
        if (currentScope == null) {
            currentScope = "[]";  // 如果scope为空，初始化为一个空的JSON数组
        }

        // 更新适用范围，增加店铺id
        String updatedScope = addShopIdToScope(currentScope, shopId);

        coupon.setScope(updatedScope);
        couponMapper.updateCouponScope(coupon);
    }

    private String addShopIdToScope(String currentScope, Integer shopId) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Integer> shopIds = objectMapper.readValue(currentScope, List.class);
            if (!shopIds.contains(shopId)) {
                shopIds.add(shopId); // 如果没有重复店铺ID，则添加
            }
            return objectMapper.writeValueAsString(shopIds); // 转换回JSON字符串
        } catch (Exception e) {
            e.printStackTrace();
            return currentScope;
        }
    }

    /*店铺修改店铺券的适用范围【改】
     * 传入参数：
     *   - 店铺券id：couponId,前端传入
     *   - 参与的商品id数组：productIds,前端传入，通过多选框进行选择，前端先展示已参与的商品，卖家可以随意选择参与商品，但至少选择一个
     * 结果：
     *   - 更新该id券的适用范围为参与的商品id数组*/
    public void updateShopCouponScope(Integer couponId, List<Integer> productIds) throws Exception {
        Coupon coupon = couponMapper.selectCouponById(couponId);
        if (coupon == null) {
            throw new Exception("优惠券不存在");
        }

        // 处理 coupon scope，将产品ID数组更新到适用范围
        String updatedScope = updateScopeForProducts(coupon.getScope(), productIds);

        coupon.setScope(updatedScope);
        couponMapper.updateCouponScope(coupon);
    }

    private String updateScopeForProducts(String currentScope, List<Integer> productIds) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Integer> existingProductIds = objectMapper.readValue(currentScope, List.class);

            // 清空并重新设置为选中的产品ID
            existingProductIds.clear();
            existingProductIds.addAll(productIds); // 将产品ID加入

            return objectMapper.writeValueAsString(existingProductIds); // 转换回JSON字符串
        } catch (Exception e) {
            e.printStackTrace();
            return "[]"; // 如果出现异常，返回一个空的JSON数组
        }
    }

    /*达到生效时间，更新所有符合条件的未生效的券的状态为已生效Active【改，自动的】*/


    /*达到失效时间，更新所有符合条件的券的状态为已失效Expired【改，自动的】*/
}

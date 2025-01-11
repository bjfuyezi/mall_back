package com.example.demo.service;

import com.example.demo.config.Utils;
import com.example.demo.enums.CouponType;
import com.example.demo.mapper.CouponMapper;
import com.example.demo.mapper.ShopMapper;
import com.example.demo.mapper.UserCouponMapper;
import com.example.demo.pojo.Coupon;
import com.example.demo.enums.CouponStatus;
import com.example.demo.pojo.Shop;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
//@Transactional
public class CouponService {
    @Autowired
    private CouponMapper couponMapper;
    @Autowired
    private UserCouponMapper userCouponMapper;
    @Autowired
    private ShopMapper shopMapper;
    private ObjectMapper objectMapper = new ObjectMapper();
    /*优惠券的创建：【增】
     * 前端需要传入的数据有：
     *   -类型coupon_type:根据前端不同身份用户创建时指定，卖家指定为shop,平台指定为platform
     *   -开始时间start_time:表单传参
     *   -结束时间end_time:表单传参
     *   -适用范围scope：平台券初始默认为空，店铺券必须初始至少支持适用一个商品，后续在生效前都可以再添加或者修改
     *   -最低消费request:表单传参
     *   -满减金额off:表单传参
     *   -状态coupon_status:创建时初始默认为Pending【待生效】，不用传入
     *   -总数量total：表单传参，表单上有默认值99999999
     *   -限制领取数量claim_limit：表单传参，表单上有默认值3
     *   -单用户账户内未使用的此优惠券最大数量max_unused_count：表单传参，表单上有默认值3
     *   -店铺id：shop_id,如果是平台券则为0
     * */
    // 加券,检查合格--浮笙
    public void createCoupon( Coupon coupon) throws Exception {
        // 使用 Utils.TimetoDate 方法解析日期
        Date start = coupon.getStart_time();
        Date end = coupon.getEnd_time();
//        System.out.println("Start: " + start);
//        System.out.println("End: " + end);
        double request = coupon.getRequest();
        double off =  coupon.getOff();
        BigInteger total = coupon.getTotal();
        int claim_limit = coupon.getClaim_limit();
        int max_unused_count = coupon.getMax_unused_count();
//        System.out.println(new Date());
        // 先进行检查
        if (request <= 0 || off <= 0) {
            throw new IllegalArgumentException("最低消费或折扣金额必须为正");
        }
        if(request<off){
            throw new IllegalArgumentException("最低消费不能小于折扣金额");
        }
        if (total.compareTo(BigInteger.ZERO) <= 0) {
            throw new IllegalArgumentException("总数量必须为正");
        }
        if (claim_limit <= 0 || max_unused_count <= 0) {
            throw new IllegalArgumentException("领取限制或未使用限制必须为正");
        }
        if (start.after(end)) {
            throw new IllegalArgumentException("开始时间不能晚于结束时间");
        }
        if (start.before(new Date())){
            throw new IllegalArgumentException("开始时间不能早于当前时间");
        }
        // 设置状态和创建时间
        coupon.setCoupon_status(CouponStatus.Pending);
        coupon.setCreate_time(new Date());
        // 调用 Mapper 插入数据库
        int rowsAffected = couponMapper.createCoupon(coupon);
        System.out.println(rowsAffected);
        if (rowsAffected <= 0) {
            throw new Exception("优惠券创建失败");
        }
    }
    
    /*删除某券【删】
     * 传入参数：
     *   - 券的id：coupon_id
     * 处理逻辑：
     *   - 先判断，券的状态，如果为Pending未生效，则可以直接删除，如果为其它状态则需要判断券是否被领取过，
     *   - 也就是用户的领取的券里面是否有该券，有则不可以删除并给出提示，没有则可以删除*/
    // 删除优惠券,检查合格--浮笙
    public boolean deleteCoupon(Integer coupon_id) {
        Coupon coupon = couponMapper.selectCouponById(coupon_id);
        if (coupon == null) {
            throw new IllegalArgumentException("优惠券不存在");
        }
        //更新当前券的状态，给出id
        CouponStatus before = coupon.getCoupon_status();
        int updateNum = couponMapper.updateCouponStatusByNowTime(coupon_id);
        if(updateNum>0){
            coupon = couponMapper.selectCouponById(coupon_id);
            System.out.println(coupon);
            System.out.println("优惠券"+coupon_id+"的状态从"+before+"变成了"+coupon.getCoupon_status());
        }
        if (coupon.getCoupon_status() == CouponStatus.Pending) {//待生效，必定没有人领取
            System.out.println("券的状态不为待生效");
            return couponMapper.deleteCouponById(coupon_id) > 0;
        }
        int claimedCount = userCouponMapper.countClaimedCoupons(coupon_id);
        if (claimedCount > 0) {
            throw new IllegalArgumentException("优惠券已被用户领取过，无法删除");
        }
        int result = couponMapper.deleteCouponById(coupon_id);
        return couponMapper.deleteCouponById(coupon_id) > 0;
    }

    /*平台管理员页面展示平台券，根据券的创建时间排序
     *   - 创建的晚的券排序更靠前
     *   - 列表形式展示
     *   - 提供的功能有：（修改）、（暂停发放）、（删除）【前端按钮实现】*/
    // 检查合格--浮笙
    public List<Coupon> getPlatformCoupons() {
        System.out.println("平台管理员页面展示平台券");
        //此时获取当前所有平台券，并更新其状态
        List<Coupon> coupons = couponMapper.selectCouponsByType(CouponType.platform);
        for(Coupon coupon:coupons){
            int coupon_id = coupon.getCoupon_id();
            CouponStatus before = coupon.getCoupon_status();
            int updateNum = couponMapper.updateCouponStatusByNowTime(coupon_id);
            if(updateNum>0){
                coupon = couponMapper.selectCouponById(coupon_id);
                System.out.println(coupon);
                System.out.println("优惠券"+coupon_id+"的状态从"+before+"变成了"+coupon.getCoupon_status());
            }
        }
        return couponMapper.selectCouponsByType(CouponType.platform);
    }


    /*店铺管理员页面展示自家店铺券，根据券的创建时间排序
     *   - 创建的晚的券排序更靠前
     *   - 列表形式展示
     *   - 提供的功能有：（修改）、（暂停发放）、（删除）【前端按钮实现】*/
    // 测试合格--浮笙
    public List<Coupon> getShopCoupons(Integer shop_id) {
        System.out.println("店铺管理员页面展示自家店铺券");
        //此时获取当前所有该店铺券，并更新其状态
        List<Coupon> coupons = couponMapper.selectCouponsByShop_id(shop_id);
        for(Coupon coupon:coupons){
            int coupon_id = coupon.getCoupon_id();
            CouponStatus before = coupon.getCoupon_status();
            int updateNum = couponMapper.updateCouponStatusByNowTime(coupon_id);
            if(updateNum>0){
                coupon = couponMapper.selectCouponById(coupon_id);
                System.out.println(coupon);
                System.out.println("优惠券"+coupon_id+"的状态从"+before+"变成了"+coupon.getCoupon_status());
            }
        }
        return couponMapper.selectCouponsByShop_id(shop_id);
    }

    /*用户页面展示平台券【查】
     * 没有传入参数
     * 返回结果：
     *   - 平台当前所有已生效Active状态的平台券*/
    // 测试合格--浮笙
    public List<Coupon> getActivePlatformCoupons() {
        //此时先获取当前所有平台券，并更新其状态
        System.out.println("用户页面展示平台券");
        List<Coupon> coupons = couponMapper.selectCouponsByType(CouponType.platform);
        for(Coupon coupon:coupons){
            int coupon_id = coupon.getCoupon_id();
            CouponStatus before = coupon.getCoupon_status();
            int updateNum = couponMapper.updateCouponStatusByNowTime(coupon_id);
            if(updateNum>0){
                coupon = couponMapper.selectCouponById(coupon_id);
                System.out.println(coupon);
                System.out.println("优惠券"+coupon_id+"的状态从"+before+"变成了"+coupon.getCoupon_status());
            }
        }
        return couponMapper.getActivePlatformCoupons(CouponType.platform);
    }


    /*用户页面展示某一店铺券【查】
     * 传入参数：
     *   - 店铺id：shop_id
     * 返回结果：
     *   - 该店铺当前所有已生效状态Active的店铺优惠券*/
    // 测试合格--浮笙
    public List<Coupon> getActiveShopCoupons(Integer shop_id) {
        System.out.println("用户页面展示某一店铺券");
        //此时获取当前所有该店铺券，并更新其状态
        List<Coupon> coupons = couponMapper.selectCouponsByShop_id(shop_id);
        for(Coupon coupon:coupons){
            int coupon_id = coupon.getCoupon_id();
            CouponStatus before = coupon.getCoupon_status();
            int updateNum = couponMapper.updateCouponStatusByNowTime(coupon_id);
            if(updateNum>0){
                coupon = couponMapper.selectCouponById(coupon_id);
                System.out.println(coupon);
                System.out.println("优惠券"+coupon_id+"的状态从"+before+"变成了"+coupon.getCoupon_status());
            }
        }
        return couponMapper.getActiveShopCoupons(shop_id);
    }

    /*券未生效前，修改券的内容【改】
     * 可以修改：开始时间、结束时间、最低消费、满减金额、总数量、限制领取数量、单用户账户内未使用的此优惠券最大数量
     * 传入券的id和要修改的内容*/
    // 测试合格
    public void updatePendingCouponContent(Integer coupon_id, String start_time, String end_time,
                                           Double request, Double off, BigInteger total, Integer claim_limit,
                                           Integer max_unused_count) throws Exception {
        Coupon coupon = couponMapper.selectCouponById(coupon_id);
        if (coupon == null) {
            throw new IllegalArgumentException("优惠券不存在");
        }
        //更新当前券的状态，给出id
        CouponStatus before = coupon.getCoupon_status();
        int updateNum = couponMapper.updateCouponStatusByNowTime(coupon_id);
        if(updateNum>0){
            coupon = couponMapper.selectCouponById(coupon_id);
            System.out.println(coupon);
            System.out.println("优惠券"+coupon_id+"的状态从"+before+"变成了"+coupon.getCoupon_status());
        }
        Date start = Utils.TimetoDate(start_time);
        Date end =Utils.TimetoDate(end_time);
        // 检查格式是否合格
        if (request <= 0 || off <= 0) {
            throw new IllegalArgumentException("最低消费或折扣金额必须为正");
        }
        if(request<off){
            throw new IllegalArgumentException("最低消费不能小于折扣金额");
        }
        if (total.compareTo(BigInteger.ZERO) <= 0) {
            throw new IllegalArgumentException("总数量必须为正");
        }
        if (claim_limit <= 0 || max_unused_count <= 0) {
            throw new IllegalArgumentException("领取限制或未使用限制必须为正");
        }
        if (start.after(end)) {
            throw new IllegalArgumentException("开始时间不能晚于结束时间");
        }
        if (start.before(new Date())){
            throw new IllegalArgumentException("开始时间不能早于当前时间");
        }
        System.out.println("updatePendingCouponContent");
        System.out.println(coupon_id);
        System.out.println(start);
        System.out.println(end);
        System.out.println(request);
        System.out.println(off);
        System.out.println(total);
        System.out.println(claim_limit);
        System.out.println(max_unused_count);
        coupon = couponMapper.selectCouponById(coupon_id);
        System.out.println(coupon);
        // 调用Mapper更新未生效券
        int rowsAffected = couponMapper.updatePendingCouponContent(coupon_id, start, end, request, off, total, claim_limit, max_unused_count);
        System.out.println(rowsAffected);
        if (rowsAffected <= 0) {
            throw new Exception("未生效优惠券修改失败");
        }
    }


    /*券生效后，修改券的内容【改】
     * 可以修改：总数量、限制领取数量、单用户账户内未使用的此优惠券最大数量
     * 传入券的id和要修改的内容*/
    // 测试合格--浮笙
    public void updateActiveCouponContent(Integer coupon_id, BigInteger total, Integer claim_limit, Integer max_unused_count) throws Exception {
        Coupon coupon = couponMapper.selectCouponById(coupon_id);
        if (coupon == null) {
            throw new IllegalArgumentException("优惠券不存在");
        }
        //更新当前券的状态，给出id
        CouponStatus before = coupon.getCoupon_status();
        int updateNum = couponMapper.updateCouponStatusByNowTime(coupon_id);
        if(updateNum>0){
            coupon = couponMapper.selectCouponById(coupon_id);
            System.out.println(coupon);
            System.out.println("优惠券"+coupon_id+"的状态从"+before+"变成了"+coupon.getCoupon_status());
        }
        if (total.compareTo(BigInteger.ZERO) <= 0) {
            throw new IllegalArgumentException("总数量必须为正");
        }
        if (claim_limit <= 0 || max_unused_count <= 0) {
            throw new IllegalArgumentException("领取限制或未使用限制必须为正");
        }
        // 调用Mapper更新已生效券
        int rowsAffected = couponMapper.updateActiveCouponContent(coupon_id, total, claim_limit, max_unused_count);
        if (rowsAffected <= 0) {
            throw new Exception("已生效优惠券修改失败");
        }
    }


    /*券生效后，暂停发放券【改】
     * 传入券的id，将该券的状态变更为Paused【暂停领取】【主要是为了避免发错券后的补救措施】
     * 前端只给生效中的券提供该按钮功能*/
    public void pauseActiveCoupon(Integer coupon_id) throws Exception {
        Coupon coupon = couponMapper.selectCouponById(coupon_id);
        if (coupon == null) {
            throw new IllegalArgumentException("优惠券不存在");
        }
        //更新当前券的状态，给出id【为了避免券实际上已经过期】
        CouponStatus before = coupon.getCoupon_status();
        int updateNum = couponMapper.updateCouponStatusByNowTime(coupon_id);
        if(updateNum>0){
            coupon = couponMapper.selectCouponById(coupon_id);
            System.out.println(coupon);
            System.out.println("优惠券"+coupon_id+"的状态从"+before+"变成了"+coupon.getCoupon_status());
            if(coupon.getCoupon_status()==CouponStatus.Expired){
                throw new IllegalArgumentException("该券已过期，状态已更新，无需暂停发放");
            }
        }
        // 调用Mapper暂停已生效券
        int rowsAffected = couponMapper.pauseActiveCoupon(coupon_id);
        if (rowsAffected <= 0) {
            throw new Exception("暂停优惠券发放失败");
        }
    }

    /*店铺加入参与某一平台券【刚开始的时候只会有未生效的券显示】【改】
     * 传入参数：
     *   - 店铺id：shop_id,前端传入
     *   - 平台券id：coupon_id,前端传入
     * 结果：
     *   将该id券的适用范围增加一个店铺id信息并更新
     * */
    // 测试合格--浮笙
    public void addShopToCoupon(Integer shop_id, Integer coupon_id) throws Exception {
        Coupon coupon = couponMapper.selectCouponById(coupon_id);
        if (coupon == null) {
            throw new Exception("优惠券不存在");
        }
        //更新当前券的状态，给出id【只有当券的状态为未生效才可以加入其中，为了避免当前状态更新了】
        CouponStatus before = coupon.getCoupon_status();
        int updateNum = couponMapper.updateCouponStatusByNowTime(coupon_id);
        if(updateNum>0){
            coupon = couponMapper.selectCouponById(coupon_id);
            System.out.println(coupon);
            System.out.println("优惠券"+coupon_id+"的状态从"+before+"变成了"+coupon.getCoupon_status());
            if(coupon.getCoupon_status()!=CouponStatus.Pending){
                throw new IllegalArgumentException("该券不处于待生效Pending状态，无法加入");
            }
        }
        Shop shop = shopMapper.selectById(shop_id);
        if(shop==null){
            throw new Exception("店铺不存在");
        }
        // 处理 coupon scope，将店铺id加入适用范围
        String currentScope = coupon.getScope();
        System.out.println("current: "+currentScope);
        if (currentScope == null) {
            currentScope = "[]";
        }

        if (isShopAlreadyInScope(currentScope, shop_id)) {
            throw new IllegalArgumentException("该商家已经在优惠券的适用范围中，无法重复添加");
        }

        // 更新适用范围，增加店铺id
        String updatedScope = addShop_idToScope(currentScope, shop_id);
        System.out.println("update: "+updatedScope);
        coupon.setScope(updatedScope);
//        System.out.println(coupon);
        couponMapper.updateCouponScope(coupon);
//        couponMapper.updateScope(coupon_id,updatedScope);
//        System.out.println(couponMapper.selectCouponById(coupon_id));
    }

    // 检查商家是否已经在适用范围中
    private boolean isShopAlreadyInScope(String currentScope, Integer shop_id) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Integer> shop_ids = objectMapper.readValue(currentScope, List.class);
            return shop_ids.contains(shop_id); // 检查是否包含指定店铺ID
        } catch (Exception e) {
            e.printStackTrace();
            return false; // 解析失败时，默认返回false
        }
    }

    private String addShop_idToScope(String currentScope, Integer shop_id) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Integer> shop_ids = objectMapper.readValue(currentScope, List.class);
            if (!shop_ids.contains(shop_id)) {
                shop_ids.add(shop_id); // 如果没有重复店铺ID，则添加
            }
            return objectMapper.writeValueAsString(shop_ids); // 转换回JSON字符串
        } catch (Exception e) {
            e.printStackTrace();
            return currentScope;
        }
    }

    /*店铺修改店铺券的适用范围【改】--需要券的状态为Pending
     * 传入参数：
     *   - 店铺券id：coupon_id,前端传入
     *   - 参与的商品id数组：product_ids,前端传入，通过多选框进行选择，前端先展示已参与的商品，卖家可以随意选择参与商品，但至少选择一个
     * 结果：
     *   - 更新该id券的适用范围为参与的商品id数组*/
    // 测试合格
    public void updateShopCouponScope(Integer coupon_id, List<Integer> product_ids) throws Exception {
        Coupon coupon = couponMapper.selectCouponById(coupon_id);
        if (coupon == null) {
            throw new Exception("优惠券不存在");
        }
        //更新当前券的状态，给出id【只有当券的状态为未生效才可以加入其中，为了避免当前状态更新了】
        CouponStatus before = coupon.getCoupon_status();
        int updateNum = couponMapper.updateCouponStatusByNowTime(coupon_id);
        coupon = couponMapper.selectCouponById(coupon_id);
        System.out.println(coupon);
        if(updateNum>0){
            System.out.println("优惠券"+coupon_id+"的状态从"+before+"变成了"+coupon.getCoupon_status());
        }
        if(coupon.getCoupon_status()!=CouponStatus.Pending){
            throw new IllegalArgumentException("该券不处于待生效Pending状态，无法加入");
        }
        System.out.println("ids:"+product_ids);
        // 处理 coupon scope，将产品ID数组更新到适用范围
        String updatedScope = updateScopeForProducts(coupon.getScope(), product_ids);
        System.out.println(updatedScope);
        coupon.setScope(updatedScope);
        System.out.println(coupon);
        couponMapper.updateCouponScope(coupon);
    }

    private String updateScopeForProducts(String currentScope, List<Integer> product_ids) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Integer> existingProduct_ids = objectMapper.readValue(currentScope, List.class);

            // 清空并重新设置为选中的产品ID
            existingProduct_ids.clear();
            existingProduct_ids.addAll(product_ids); // 将产品ID加入

            return objectMapper.writeValueAsString(existingProduct_ids); // 转换回JSON字符串
        } catch (Exception e) {
            e.printStackTrace();
            return "[]"; // 如果出现异常，返回一个空的JSON数组
        }
    }
}

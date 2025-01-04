package com.example.demo.router;

import com.example.demo.enums.CouponType;
import com.example.demo.pojo.Coupon;
import com.example.demo.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/coupon")
public class CouponRouter {
    @Autowired
    private CouponService couponService;

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
    @PostMapping("/create")
    public ResponseEntity<String> createCoupon(
            @RequestParam("coupon_type") CouponType couponType,
            @RequestParam("start_time") String startTime,
            @RequestParam("end_time") String endTime,
            @RequestParam("scope") String scope,
            @RequestParam("request") double request,
            @RequestParam("off") double off,
            @RequestParam("total") BigInteger total,
            @RequestParam("claim_limit") Integer claimLimit,
            @RequestParam("max_unused_count") Integer maxUnusedCount,
            @RequestParam("shop_id") Integer shopId
    ) {
        try {
            couponService.createCoupon(couponType,startTime,endTime,scope,
                    request, off, total, claimLimit, maxUnusedCount,shopId);
            return ResponseEntity.status(HttpStatus.CREATED).body("优惠券创建成功");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("创建失败: " + e.getMessage());
        }
    }

    /*删除某券【删】
    * 传入参数：
    *   - 券的id：couponId
    * 处理逻辑：
    *   - 先判断，券的状态，如果为Pending未生效，则可以直接删除，如果为其它状态则需要判断券是否被领取过，
    *   - 也就是用户的领取的券里面是否有该券，有则不可以删除并给出提示，没有则可以删除*/
    @DeleteMapping("/delete/{couponId}")
    public ResponseEntity<String> deleteCoupon(@PathVariable Integer couponId) {
        boolean result = couponService.deleteCoupon(couponId);
        if (result) {
            return ResponseEntity.ok("优惠券删除成功");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("优惠券无法删除");
    }

    /*平台管理员页面展示平台券，根据券的创建时间排序
    *   - 创建的晚的券排序更靠前
    *   - 列表形式展示
    *   - 提供的功能有：（修改）、（暂停发放）、（删除）【前端按钮实现】*/
    @GetMapping("/platform")
    public ResponseEntity<List<Coupon>> getPlatformCoupons() {
        return ResponseEntity.ok(couponService.getPlatformCoupons());
    }

    /*店铺管理员页面展示自家店铺券，根据券的创建时间排序
     *   - 创建的晚的券排序更靠前
     *   - 列表形式展示
     *   - 提供的功能有：（修改）、（暂停发放）、（删除）【前端按钮实现】*/
    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<Coupon>> getShopCoupons(@PathVariable Integer shopId) {
        return ResponseEntity.ok(couponService.getShopCoupons(shopId));
    }

    /*用户页面展示平台券【查】
    * 没有传入参数
    * 返回结果：
    *   - 平台当前所有已生效Active状态的平台券*/
    @GetMapping("/platform/active")
    public ResponseEntity<List<Coupon>> getActivePlatformCoupons() {
        List<Coupon> coupons = couponService.getActivePlatformCoupons();
        return ResponseEntity.ok(coupons);
    }


    /*用户页面展示某一店铺券【查】
    * 传入参数：
    *   - 店铺id：shopId
    * 返回结果：
    *   - 该店铺当前所有已生效状态Active的店铺优惠券*/
    @GetMapping("/shop/{shopId}/active")
    public ResponseEntity<List<Coupon>> getActiveShopCoupons(@PathVariable Integer shopId) {
        List<Coupon> coupons = couponService.getActiveShopCoupons(shopId);
        return ResponseEntity.ok(coupons);
    }

    /*券未生效前，修改券的内容【改】
    * 可以修改：开始时间、结束时间、适用范围、最低消费、满减金额、总数量、限制领取数量、单用户账户内未使用的此优惠券最大数量
    * 传入券的id和要修改的内容*/
    @PutMapping("/update/pending/{couponId}")
    public ResponseEntity<String> updatePendingCoupon(
            @PathVariable Integer couponId,
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam Double request,
            @RequestParam Double off,
            @RequestParam BigInteger total,
            @RequestParam Integer claimLimit,
            @RequestParam Integer maxUnusedCount) {
        try {
            couponService.updatePendingCouponContent(couponId, startTime, endTime, request, off, total, claimLimit, maxUnusedCount);
            return ResponseEntity.ok("未生效优惠券修改成功");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("未生效优惠券修改失败: " + e.getMessage());
        }
    }


    /*券生效后，修改券的内容【改】
    * 可以修改：总数量、限制领取数量、单用户账户内未使用的此优惠券最大数量
    * 传入券的id和要修改的内容*/
    @PutMapping("/update/active/{couponId}")
    public ResponseEntity<String> updateActiveCoupon(
            @PathVariable Integer couponId,
            @RequestParam Integer total,
            @RequestParam Integer claimLimit,
            @RequestParam Integer maxUnusedCount) {

        try {
            couponService.updateActiveCouponContent(couponId, total, claimLimit, maxUnusedCount);
            return ResponseEntity.ok("已生效优惠券修改成功");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("已生效优惠券修改失败: " + e.getMessage());
        }
    }


    /*券生效后，暂停发放券【改】
    * 传入券的id，将该券的状态变更为Paused【暂停领取】【主要是为了避免发错券后的补救措施】*/
    @PutMapping("/pause/{couponId}")
    public ResponseEntity<String> pauseActiveCoupon(@PathVariable Integer couponId) {

        try {
            couponService.pauseActiveCoupon(couponId);
            return ResponseEntity.ok("优惠券暂停发放成功");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("优惠券暂停发放失败: " + e.getMessage());
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
    @PostMapping("/addShopToCoupon")
    public ResponseEntity<String> addShopToCoupon(
            @RequestParam Integer shopId,
            @RequestParam Integer couponId
    ) throws Exception {
        couponService.addShopToCoupon(shopId, couponId);
        return ResponseEntity.ok("Shop successfully added to the coupon");
    }

    /*店铺修改店铺券的适用范围【改】
    * 传入参数：
    *   - 店铺券id：couponId,前端传入
    *   - 参与的商品id数组：productIds,前端传入，通过多选框进行选择，前端先展示已参与的商品，卖家可以随意选择参与商品，但至少选择一个
    * 结果：
    *   - 更新该id券的适用范围为参与的商品id数组*/
    @PostMapping("/updateShopCouponScope")
    public ResponseEntity<String> updateShopCouponScope(
            @RequestParam Integer couponId,
            @RequestParam List<Integer> productIds
    ) throws Exception {
        couponService.updateShopCouponScope(couponId, productIds);
        return ResponseEntity.ok("Shop coupon scope updated successfully");
    }

    /*达到生效时间，更新所有符合条件的未生效的券的状态为已生效Active【改，自动的，定时】*/


    /*达到失效时间，更新所有符合条件的券的状态为已失效Expired【改，自动的，定时】*/


}

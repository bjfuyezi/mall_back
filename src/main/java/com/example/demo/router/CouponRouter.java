package com.example.demo.router;

import com.example.demo.config.Utils;
import com.example.demo.enums.CouponStatus;
import com.example.demo.enums.CouponType;
import com.example.demo.pojo.Coupon;
import com.example.demo.pojo.Product;
import com.example.demo.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/coupon")
public class CouponRouter {
    @Autowired
    private CouponService couponService;

    /*优惠券的创建：【增】
     * 前端需要传入的数据有：
     *   - 类型coupon_type: 根据前端不同身份用户创建时指定，卖家指定为shop,平台指定为platform
     *   - 开始时间start_time: 表单传参
     *   - 结束时间end_time: 表单传参
     *   - 适用范围scope：平台券初始默认为空，店铺券必须初始至少支持适用一个商品，后续在生效前都可以再添加或者修改
     *   - 最低消费request: 表单传参
     *   - 满减金额off: 表单传参
     *   - 状态coupon_status: 创建时初始默认为Pending【待生效】，不用传入
     *   - 总数量total：表单传参，表单上有默认值99999999
     *   - 限制领取数量claim_limit：表单传参，表单上有默认值3
     *   - 单用户账户内未使用的此优惠券最大数量max_unused_count：表单传参，表单上有默认值3
     *   - 店铺id：shop_id,如果是平台券则为0
     * */
    /*预期传入的参数：[示例1]
    * {
    *   coupon_type:shop
    *   start_time:2025-01-05 00:00:00
    *   end_time:2025-02-05 00:00:00
    *   scope:[1,2]
    *   request:100
    *   off:10
    *   total:9999999999
    *   claim_limit:5
    *   max_unused_count:3
    *   shop_id:1
    * }
    * 【示例2】
    * {
     *   coupon_type:platform
     *   start_time:2025-01-06 00:00:00
     *   end_time:2025-02-06 00:00:00
     *   scope:[1,2]
     *   request:100
     *   off:10
     *   total:9999999999
     *   claim_limit:5
     *   max_unused_count:3
     *   shop_id:0
     * }*/
    @PostMapping("/create")
    public ResponseEntity<String> createCoupon(
            @RequestBody Map<String,Object> requestBody
    ) {
        try {
            String coupon_type = (String) requestBody.get("coupon_type");
            String start_time = (String) requestBody.get("start_time");
            String end_time = (String) requestBody.get("end_time");
            Double request = (Double) requestBody.get("request");
            Double off = (Double) requestBody.get("off");
            String scope = (String) requestBody.get("scope");
            BigInteger total = new BigInteger(requestBody.get("total").toString());
            Integer claim_limit = (Integer) requestBody.get("claim_limit");
            Integer max_unused_count = (Integer) requestBody.get("max_unused_count");
            Integer shop_id = (Integer) requestBody.get("shop_id");

            couponService.createCoupon(coupon_type,start_time,end_time,scope,request,off,total,claim_limit,max_unused_count,shop_id);
            System.out.println("ok");
            return ResponseEntity.status(HttpStatus.OK).body("优惠券创建成功");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("创建失败,格式不符合要求: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("创建失败: " + e.getMessage());
        }
    }
//    public ResponseEntity<String> createCoupon(
//            @RequestBody Coupon coupon
//    ) {
//        try {
////            System.out.println("Coupon Type: " + coupon.getCoupon_type());
////            System.out.println("Start Time class" + coupon.getStart_time().getClass());
////            System.out.println("Start Time: " + coupon.getStart_time());
////            System.out.println("End Time: " + coupon.getEnd_time());
////            System.out.println("Scope: " + coupon.getScope());
////            System.out.println("Request: " + coupon.getRequest());
////            System.out.println("Off: " + coupon.getOff());
////            System.out.println("Total: " + coupon.getTotal());
////            System.out.println("Claim Limit: " + coupon.getClaim_limit());
////            System.out.println("Max Unused Count: " + coupon.getMax_unused_count());
////            System.out.println("Shop ID: " + coupon.getShop_id());
//            couponService.createCoupon(coupon);
//            System.out.println("ok");
//            return ResponseEntity.status(HttpStatus.OK).body("优惠券创建成功");
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("创建失败,格式不符合要求: " + e.getMessage());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("创建失败: " + e.getMessage());
//        }
//    }

    /*删除某券【删】
    * 传入参数：
    *   - 券的id：coupon_id
    * 处理逻辑：
    *   - 先判断，券的状态，如果为Pending未生效，则可以直接删除，如果为其它状态则需要判断券是否被领取过，
    *   - 也就是用户的领取的券里面是否有该券，有则不可以删除并给出提示，没有则可以删除*/
    @DeleteMapping("/delete/{coupon_id}")
    public ResponseEntity<String> deleteCoupon(@PathVariable Integer coupon_id) {
        System.out.println("要删除的券id: "+coupon_id);
        try {
            boolean result = couponService.deleteCoupon(coupon_id);
            if (result) {
                return ResponseEntity.ok("优惠券删除成功");
            }else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("优惠券删除失败");
            }
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("优惠券删除失败："+e.getMessage());
        }
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
    @GetMapping("/shop/{shop_id}")
    public ResponseEntity<List<Coupon>> getShopCoupons(@PathVariable Integer shop_id) {
        return ResponseEntity.ok(couponService.getShopCoupons(shop_id));
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
    *   - 店铺id：shop_id
    * 返回结果：
    *   - 该店铺当前所有已生效状态Active的店铺优惠券*/
    @GetMapping("/shop/{shop_id}/active")
    public ResponseEntity<List<Coupon>> getActiveShopCoupons(@PathVariable Integer shop_id) {
        List<Coupon> coupons = couponService.getActiveShopCoupons(shop_id);
        return ResponseEntity.ok(coupons);
    }

    /*券未生效前，修改券的内容【改】
    * 可以修改：开始时间、结束时间、适用范围、最低消费、满减金额、总数量、限制领取数量、单用户账户内未使用的此优惠券最大数量
    * 传入券的id和要修改的内容*/
    @PostMapping("/update/pending")
    public ResponseEntity<String> updatePendingCoupon(
            @RequestBody Map<String,Object> requestBody ) {
        try {
            Integer coupon_id = (Integer) requestBody.get("coupon_id");
            String start_time = (String) requestBody.get("start_time");
            String end_time = (String) requestBody.get("end_time");
            double request = ((Integer) requestBody.get("request")).doubleValue();
            double off = ((Integer) requestBody.get("off")).doubleValue();
            BigInteger total = new BigInteger(requestBody.get("total").toString());
            Integer claim_limit = (Integer) requestBody.get("claim_limit");
            Integer max_unused_count = (Integer) requestBody.get("max_unused_count");
            System.out.println(coupon_id);
            System.out.println(start_time);
            System.out.println(end_time);
            System.out.println(request);
            System.out.println(off);
            System.out.println(total);
            System.out.println(claim_limit);
            System.out.println(max_unused_count);
            couponService.updatePendingCouponContent(coupon_id, start_time, end_time, request, off, total, claim_limit, max_unused_count);
            return ResponseEntity.ok("未生效优惠券修改成功");
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("修改数据不合格: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("未生效优惠券修改失败: " + e.getMessage());
        }
    }

    /*券生效后，修改券的内容【改】
    * 可以修改：总数量、限制领取数量、单用户账户内未使用的此优惠券最大数量
    * 传入券的id和要修改的内容*/
    @PostMapping("/update/active")
    public ResponseEntity<String> updateActiveCoupon(
            @RequestBody Map<String,Object> requestBody) {
        try {
            BigInteger total = new BigInteger(requestBody.get("total").toString());
            Integer claim_limit = (Integer) requestBody.get("claim_limit");
            Integer max_unused_count = (Integer) requestBody.get("max_unused_count");
            Integer coupon_id = (Integer) requestBody.get("coupon_id");
            System.out.println(coupon_id);
            System.out.println(total);
            System.out.println(claim_limit);
            System.out.println(max_unused_count);
            couponService.updateActiveCouponContent(coupon_id, total, claim_limit, max_unused_count);
            return ResponseEntity.ok("已生效优惠券修改成功");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("已生效优惠券修改失败: " + e.getMessage());
        }
    }


    /*券生效后，暂停发放券【改】
    * 传入券的id，将该券的状态变更为Paused【暂停领取】【主要是为了避免发错券后的补救措施】*/
    @PutMapping("/pause")
    public ResponseEntity<String> pauseActiveCoupon(@RequestParam Integer coupon_id) {
        try {
            couponService.pauseActiveCoupon(coupon_id);
            return ResponseEntity.ok("优惠券暂停发放成功");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("优惠券暂停发放失败: " + e.getMessage());
        }
    }

    @PutMapping("/resume")
    public ResponseEntity<String> resumeCoupon(@RequestParam Integer coupon_id) {
        try {
            couponService.resumeCoupon(coupon_id);
            return ResponseEntity.ok("优惠券恢复领取成功");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("优惠券恢复领取失败: " + e.getMessage());
        }
    }

    // TODO:恢复领取


    /*券生效前，不想发放券了，需要点击一个什么按钮【改/删】（todo:按钮想法
    * 传入券的id,将该券的状态设置为已失效，或者直接删除该券，此时该券必定没有用户领取【这个在删除券的地方已经实现了】*/

    /*店铺加入参与某一平台券【改】
    * 传入参数：
    *   - 店铺id：shop_id,前端传入
    *   - 平台券id：coupon_id,前端传入
    * 结果：
    *   将该id券的适用范围增加一个店铺id信息并更新*/
    @PostMapping("/addShopToCoupon")
    public ResponseEntity<String> addShopToCoupon(
            @RequestParam Integer shop_id,
            @RequestParam Integer coupon_id
    ){
        try{
            couponService.addShopToCoupon(shop_id, coupon_id);
            return ResponseEntity.ok("商家已经加入该平台券中");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("添加失败："+e.getMessage());
        }

    }

    /*店铺修改店铺券的适用范围【改】
    * 传入参数：
    *   - 店铺券id：coupon_id,前端传入
    *   - 参与的商品id数组：product_ids,前端传入，通过多选框进行选择，前端先展示已参与的商品，卖家可以随意选择参与商品，但至少选择一个
    * 结果：
    *   - 更新该id券的适用范围为参与的商品id数组*/
    @PostMapping("/updateShopCouponScope")
    public ResponseEntity<String> updateShopCouponScope(
            @RequestBody Map<String,Object> requestBody
    ) throws Exception {
        try{
            Integer coupon_id = (Integer) requestBody.get("coupon_id");
            List<Integer> product_ids = (List<Integer>) requestBody.get("productIds");
            couponService.updateShopCouponScope(coupon_id, product_ids);
            return ResponseEntity.ok("店铺券范围已更新成功");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("更新失败："+e.getMessage());
        }
    }

    @PostMapping("/selectById")
    public ResponseEntity<Coupon> selectById(@RequestBody Map<String, Object> request) {
        try {
            Integer id = (Integer) request.get("id");
            Coupon t = couponService.selectById(id);
            if (t != null) {
                return new ResponseEntity<>(t, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            // 记录异常信息到日志
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);//500 error
        }
    }
//    @PostMapping("/updateShopCouponScope")
//    public ResponseEntity<String> updateShopCouponScope(
//            @RequestParam Integer coupon_id,
//            @RequestParam List<Integer> product_ids
//    ) throws Exception {
//        couponService.updateShopCouponScope(coupon_id, product_ids);
//        return ResponseEntity.ok("Shop coupon scope updated successfully");
//    }

    /*达到生效时间，更新所有符合条件的未生效的券的状态为已生效Active【改，自动的，定时】*/


    /*达到失效时间，更新所有符合条件的券的状态为已失效Expired【改，自动的，定时】*/


}

package com.example.demo.router;

import com.example.demo.service.CouponService;
import com.example.demo.service.UserCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/userCoupon")
public class UserCouponRouter {
    @Autowired
    private UserCouponService userCouponService;
    @Autowired
    private CouponService couponService;
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
    @PostMapping("/claim")
    public ResponseEntity<String> claimCoupon(
            @RequestBody Map<String,Object> requestBody
    ) {
        Integer user_id = (Integer) requestBody.get("user_id");
        Integer coupon_id = (Integer) requestBody.get("coupon_id");
        String message = userCouponService.claimCoupon(user_id, coupon_id);
        if(message.equals("优惠券领取成功")){
            return ResponseEntity.ok(message); // 返回状态码 200，并携带业务消息
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }
    /*用户查看券列表（全部）【查】
    * 列表展示有一定的要求，要求如下：
    *   - 优先展示所有平台券，平台券按照领取时间，晚领取的优先展示
    *   - 展示完所有平台券后，根据店铺进行分块展示，展示具体要求如下：
    *       - 每个店铺下的券按照领取时间排序，晚领取的优先展示
    *       - 店铺分块展示的顺序由每个店铺中最晚领取的券的时间决定，这个时间越晚，排序越靠前
    *   - 这里不区分券的状态，全部展示*/
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getUserCoupons(@RequestParam("user_id") Integer user_id) {
//        Integer user_id = (Integer) requestBody.get("user_id");
        System.out.println("userCoupon/list获得数据："+user_id);
        // ResponseEntity<?> 可以返回任意类型的数据，同时还可以灵活地设置 HTTP 状态码和头信息。
        Map<String, Object> couponList = userCouponService.getUserCoupons(user_id);
        return ResponseEntity.ok(couponList);
    }

    /*用户查看券列表（根据状态分类）【查】
     * 列表展示有一定的要求，要求如下：
     *   - 优先展示所有平台券，平台券按照领取时间，晚领取的优先展示
     *   - 展示完所有平台券后，根据店铺进行分块展示，展示具体要求如下：
     *       - 每个店铺下的券按照领取时间排序，晚领取的优先展示
     *       - 店铺分块展示的顺序由每个店铺中最晚领取的券的时间决定，这个时间越晚，排序越靠前
     *   - 这里区分券的状态，需要传入券的某一状态，将这一状态的券按照上述要求返回给前端*/
    @GetMapping("/listByStatus")
    public ResponseEntity<Map<String, Object>> getUserCouponsByStatus(@RequestBody Map<String,Object> requestBody) {
        Integer user_id = (Integer) requestBody.get("user_id");
        String status = (String) requestBody.get("status");
        System.out.println(user_id);
        System.out.println(status);
        Map<String, Object> couponList = userCouponService.getUserCouponsByStatus(user_id, status);
        return ResponseEntity.ok(couponList);
    }

    /*用户查看券的适用店铺或者适用商品【查】
    * 传入参数：
    *   - 券的id:coupon_id*/
    @GetMapping("/scope/details")
    public ResponseEntity<Map<String, List<Object>>> getCouponScopeDetails(@RequestParam("coupon_id") Integer coupon_id) {
        try {
//            Integer coupon_id = (Integer) requestBody.get("coupon_id");
            System.out.println(coupon_id);
            Map<String, List<Object>> scopeDetails = userCouponService.getCouponScopeDetails(coupon_id);
            return ResponseEntity.ok(scopeDetails);
        } catch (Exception e) {
            e.printStackTrace();
            return (ResponseEntity<Map<String, List<Object>>>) ResponseEntity.badRequest();
        }
    }

    /*用户使用券，券的类型进行更改为Used【改】
    * 传入参数：
    *   - 用户id:user_id
    *   - 券的id:coupon_id*/
    @PutMapping("/use")
    public ResponseEntity<String> useCoupon(@RequestBody Map<String,Object> requestBody) {
        try{
            Integer user_coupon_id = (Integer) requestBody.get("user_coupon_id");
            System.out.println(user_coupon_id);
            boolean success = userCouponService.useCoupon(user_coupon_id);
            if(success){
                return ResponseEntity.ok("使用成功");
            }else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("使用失败，可能优惠券已失效或不存在！");
            }
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("使用失败："+e.getMessage());
        }

//        return success ? "使用成功！" : "使用失败，可能优惠券已失效或不存在！";
    }

    @PostMapping("/getAvailable")
    public ResponseEntity<Map<String,List<Object>>> getAvailableByProduct_ids(@RequestBody Map<String,Object> requestBody){
        Integer user_id = (Integer) requestBody.get("used_id");

        return null;
    }
}

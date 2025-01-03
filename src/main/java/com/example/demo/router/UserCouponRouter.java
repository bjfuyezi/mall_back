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
@RequestMapping("userCoupon")
public class UserCouponRouter {
    @Autowired
    private UserCouponService userCouponService;
    @Autowired
    private CouponService couponService;
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
    @PostMapping("/claim")
    public ResponseEntity<String> claimCoupon(
            @RequestParam Integer userId,
            @RequestParam Integer couponId
    ) {
        String message = userCouponService.claimCoupon(userId, couponId);
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
    public ResponseEntity<?> getUserCoupons(@RequestParam Integer userId) {
        // ResponseEntity<?> 可以返回任意类型的数据，同时还可以灵活地设置 HTTP 状态码和头信息。
        Map<String, Object> couponList = userCouponService.getUserCoupons(userId);
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
    public ResponseEntity<?> getUserCouponsByStatus(@RequestParam Integer userId,
                                                    @RequestParam String status) {
        Map<String, Object> couponList = userCouponService.getUserCouponsByStatus(userId, status);
        return ResponseEntity.ok(couponList);
    }

    /*用户查看券的适用店铺或者适用商品【查】
    * 传入参数：
    *   - 券的id:couponId*/
    @GetMapping("/scope/details/{couponId}")
    public ResponseEntity<Map<String, List<Object>>> getCouponScopeDetails(@PathVariable Integer couponId) {
        try {
            Map<String, List<Object>> scopeDetails = userCouponService.getCouponScopeDetails(couponId);
            return ResponseEntity.ok(scopeDetails);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); // 参数错误
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build(); // 服务器错误
        }
    }

    /*用户使用券，券的类型进行更改为Used【改】
    * 传入参数：
    *   - 用户id:userId
    *   - 券的id:couponId*/
    @PutMapping("/use")
    public String useCoupon(@RequestParam Integer userId, @RequestParam Integer couponId) {
        boolean success = userCouponService.useCoupon(userId, couponId);
        return success ? "使用成功！" : "使用失败，可能优惠券已失效或不存在！";
    }

    /*券达到失效时间，更改状态为已失效Expired【改】
    * 传入参数：
    *   - 用户id:userId
    *   - 券的id:couponId*/
}

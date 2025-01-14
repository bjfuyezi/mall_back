package com.example.demo.router;

import com.example.demo.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/*
cart: 购物车
@RestController restful方式返回前端，具体格式code标准百度查询
@RestController 是一个组合注解，它结合了 @Controller 和 @ResponseBody 注解的功能（就相当于把两个注解组合在一起）。
在使用 @RestController 注解标记的类中，每个方法的返回值都会以 JSON 或 XML 的形式直接写入 HTTP 响应体中，
相当于在每个方法上都添加了 @ResponseBody 注解。

@RequestMapping("/advertise") 网页前缀，
@GetMapping("/")表示具体请求方式和路径，前端使用这个url即可获取
对应的还有@GetMapping、@PostMapping、@PutMapping、@DeleteMapping
new ResponseEntity<>(HttpStatus.ok) 用于规范返回code，便于前端处理，
否则全为200ok难debug，具体HttpStatus返回规范遵循restful

注意：router相当于controller，不要在这里写具体业务，这里只负责获取request并构造response，具体业务放到service，通过函数调用实现
 */
/*CartRouter 类负责处理与购物车相关的 HTTP 请求，它是一个 RESTful 风格的控制器。
使用 @RestController 注解表示该类中的方法将直接返回 JSON 或 XML 格式的数据。
所有请求前缀都以 "/cart" 开头，后续的具体方法通过不同的 HTTP 方法处理不同的请求。
在此类中，我们将 HTTP 请求传递给 CartService 类，该类包含实际的业务逻辑。

Restful API 是通过 HTTP 动作（如 GET、POST、PUT、DELETE）来处理资源，使用标准的 HTTP 状态码来表示请求的处理结果。*/
@RestController
@RequestMapping("/cart")// 所有方法的请求路径都会以 "/cart" 开头
public class CartRouter {
    @Autowired
    private CartService cartService;// 自动注入 CartService 服务类，业务逻辑将在 CartService 中处理

    /**
     * 获取用户购物车商品列表
     * @return 返回用户的购物车商品列表
     */
    @GetMapping("/items")  // 使用 GET 方法来获取用户购物车商品
    public ResponseEntity<List<Map<String, Object>>> getCartItems(@RequestParam("user_id") Integer user_id) {
        System.out.println("cart/items获取到数据："+user_id);
        // 调用 CartService 获取用户购物车商品列表，返回一个包含商品信息的列表
        return ResponseEntity.ok(cartService.getCartItemsByUser_id(user_id));  // 使用 ResponseEntity 返回 HTTP 响应
    }

    /**
     * 用户加入商品到购物车
    {
        "user_id":1,
        "product_id":1,
        "quantity":1,
        "shop_id":1
    }
     */
    @PostMapping("/add")
    public ResponseEntity<String> addProductToCart(@RequestBody Map<String,Object> requestBody) {
        try{
            Integer user_id = (Integer) requestBody.get("user_id");
            Integer product_id = (Integer) requestBody.get("product_id");
            Integer quantity = (Integer) requestBody.get("quantity");
            Integer shop_id = (Integer) requestBody.get("shop_id");
            String flavor = (String) requestBody.get("flavor");
            System.out.println(user_id);
            System.out.println(product_id);
            System.out.println(quantity);
            System.out.println(shop_id);
            System.out.println(flavor);
            boolean isAdded = cartService.addProductToCart(user_id, product_id, quantity, shop_id,flavor);
            if (isAdded) {
                return ResponseEntity.ok("商品加入购物车成功");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("加入失败");
            }
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("加入失败："+e.getMessage());
        }

    }
//    @PostMapping("/add")
//    public ResponseEntity<String> addProductToCart(@RequestParam int user_id,
//                                                   @RequestParam int product_id,
//                                                   @RequestParam int quantity,
//                                                   @RequestParam int shop_id) {
//        boolean isAdded = cartService.addProductToCart(user_id, product_id, quantity, shop_id);
//        if (isAdded) {
//            return ResponseEntity.status(HttpStatus.CREATED).body("商品已加入购物车");
//        } else {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("加入购物车失败");
//        }
//    }

    /**
     * 更新购物车商品数量
     * @return 是否成功更新购物车商品数量
     */
    @PutMapping("/update")
    public ResponseEntity<String> updateCartItemQuantity(@RequestBody Map<String,Object> requestBody) {
        try{
            Integer cart_item_id = (Integer) requestBody.get("cart_item_id");
            Integer quantity = (Integer) requestBody.get("quantity");
            System.out.println(cart_item_id);
            System.out.println(quantity);
            boolean isUpdated = cartService.updateCartItemQuantity(cart_item_id, quantity);
            if (isUpdated) {
                return ResponseEntity.ok("购物车商品数量更新成功");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("更新商品数量失败");
            }
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("更新失败："+e.getMessage());
        }
    }
//    @PutMapping("/update")
//    public ResponseEntity<String> updateCartItemQuantity(@RequestParam int user_id,
//                                                         @RequestParam int product_id,
//                                                         @RequestParam int quantity) {
//        boolean isUpdated = cartService.updateCartItemQuantity(user_id, product_id, quantity);
//        if (isUpdated) {
//            return ResponseEntity.ok("购物车商品数量更新成功");
//        } else {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("更新商品数量失败");
//        }
//    }

    /**
     * 删除购物车商品
     * @return 删除是否成功
     */
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteCartItem(@RequestBody Map<String,Object> requestBody) {
        try{
            Integer cart_item_id = (Integer) requestBody.get("cart_item_id");
            boolean isDeleted = cartService.deleteCartItem(cart_item_id);
            if (isDeleted) {
                return ResponseEntity.ok("商品已从购物车中删除");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("删除商品失败");
            }
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("删除失败:"+e.getMessage());
        }
    }

    // 一次性删除多个购物车项
    @DeleteMapping("/deleteMany")
    public ResponseEntity<String> deleteCartItems(@RequestBody Map<String,Object> requestBody) {
        try{
            List<Integer> cart_item_ids = (List<Integer>) requestBody.get("cart_item_ids");
            System.out.println("deleteMany获取数据:"+cart_item_ids);
            boolean isDeleted = cartService.deleteCartItems(cart_item_ids);
            if (isDeleted) {
                return ResponseEntity.ok("商品均已从购物车中删除");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("删除商品失败");
            }
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("删除失败:"+e.getMessage());
        }
    }

    /*TODO:购物车和结算流程
    - 用户选择商品并勾选使用的优惠券
        - 根据商品所属商铺，选择适用的店铺券或平台券
        - 计算满减金额并减去优惠
        - 判断每种券的适用条件（是否满足满减规则、未超过限制）
    - 优惠计算后生成最终总价*/

}

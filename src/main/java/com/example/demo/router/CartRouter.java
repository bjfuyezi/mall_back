package com.example.demo.router;

import com.example.demo.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     *
     * @param userId 用户ID
     * @return 返回用户的购物车商品列表
     */
    @GetMapping("/items")  // 使用 GET 方法来获取用户购物车商品
    public ResponseEntity<?> getCartItems(@RequestParam int userId) {
        // 调用 CartService 获取用户购物车商品列表，返回一个包含商品信息的列表
        return ResponseEntity.ok(cartService.getCartItemsByUserId(userId));  // 使用 ResponseEntity 返回 HTTP 响应
    }

    /**
     * 用户加入商品到购物车
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @param quantity  商品数量
     * @param shopId    商品所属店铺ID
     * @return 是否成功加入购物车
     */
    @PostMapping("/add")
    public ResponseEntity<String> addProductToCart(@RequestParam int userId,
                                                   @RequestParam int productId,
                                                   @RequestParam int quantity,
                                                   @RequestParam int shopId) {
        boolean isAdded = cartService.addProductToCart(userId, productId, quantity, shopId);
        if (isAdded) {
            return ResponseEntity.status(HttpStatus.CREATED).body("商品已加入购物车");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("加入购物车失败");
        }
    }

    /**
     * 更新购物车商品数量
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @param quantity  更新后的商品数量
     * @return 是否成功更新购物车商品数量
     */
    @PutMapping("/update")
    public ResponseEntity<String> updateCartItemQuantity(@RequestParam int userId,
                                                         @RequestParam int productId,
                                                         @RequestParam int quantity) {
        boolean isUpdated = cartService.updateCartItemQuantity(userId, productId, quantity);
        if (isUpdated) {
            return ResponseEntity.ok("购物车商品数量更新成功");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("更新商品数量失败");
        }
    }

    /**
     * 删除购物车商品
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @return 删除是否成功
     */
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteCartItem(@RequestParam int userId,
                                                 @RequestParam int productId) {
        boolean isDeleted = cartService.deleteCartItem(userId, productId);
        if (isDeleted) {
            return ResponseEntity.ok("商品已从购物车中删除");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("删除商品失败");
        }
    }
}

package com.example.demo.router;

import com.example.demo.Exception.NameException;
import com.example.demo.enums.ProductStatus;
import com.example.demo.enums.ProductType;
import com.example.demo.pojo.Advertise;
import com.example.demo.pojo.Product;
import com.example.demo.pojo.Shop;
import com.example.demo.service.PictureService;
import com.example.demo.service.ProductService;
import com.example.demo.service.ShopService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/*
@RestController restful方式返回前端，具体格式code标准百度查询
@RequestMapping("/advertise") 网页前缀，例如http://localhost:8081/advertise
@GetMapping("/")表示具体请求方式和路径，例如/表示http://localhost:8081/advertise/，前端使用这个url即可获取
对应的还有@GetMapping、@PostMapping、@PutMapping、@DeleteMapping
new ResponseEntity<>(HttpStatus.ok) 用于规范返回code，便于前端处理，否则全为200ok难debug，具体HttpStatus返回规范遵循restful

注意：router相当于controller，不要在这里写具体业务，这里只负责获取request并构造response，具体业务放到service，通过函数调用实现
 */

@RestController
@RequestMapping("/product")
public class ProductRouter {

    @Autowired
    private ProductService productService;
    @Autowired
    private PictureService pictureService;
    @Autowired
    private ShopService shopService;

    /**
     * 查找指定店铺ID的销量。
     *
     * @param request     查询体
     * @return 如果成功，则返回总销量；如果未找到，则返回 null。
     */
    @PostMapping("/getSalenumByShop_id")
    public ResponseEntity<Integer> getSalenumByShop_id(@RequestBody Map<String, Object> request) {
        Integer id = (Integer) request.get("id");
        try {
            Integer t = productService.getSalenumByShop_id(id);
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

    /**
     * 查找指定店铺ID的商品。
     *
     * @param request     查询体
     * @return 如果成功，则返回商品列表；如果未找到对应的商品，则返回 null。
     */
    @PostMapping("/getAllByShop_id")
    public ResponseEntity<List<Product>> getAllByShop_id(@RequestBody Map<String, Object> request) {
        Integer id = (Integer) request.get("id");
        try {
            List<Product> t = productService.getAllByShop_id(id);
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

    /**
     * 删除商品。
     *
     * @param
     * @return 如果成功，则返回 shop 实体；如果未找到对应的店铺，则返回 null。
     */
    @PostMapping("/deleteById")
    public ResponseEntity<String> deleteById(@RequestBody Map<String, Object> request) {
        Integer id = (Integer) request.get("id");
        String result = productService.deleteById(id);
        if (Objects.equals(result, "404")) {
            return new ResponseEntity<>("店铺不存在", HttpStatus.NOT_FOUND);
        } else if (Objects.equals(result, "409")) {
            return new ResponseEntity<>("商品名称重复", HttpStatus.CONFLICT);
        }
        // 返回成功响应
        return ResponseEntity.ok("商品删除成功");
    }

    /**
     * 更新商品库存。
     *
     * @param
     * @return 如果成功，则返回 shop 实体；如果未找到对应的店铺，则返回 null。
     */
    @PostMapping("/updateQuantity")
    public ResponseEntity<String> updateQuantity(
            @RequestParam("id") Integer id,
            @RequestParam("quantity") String quantityJson) {
        String result = productService.updateQuantity(id, quantityJson);
        if (Objects.equals(result, "404")) {
            return new ResponseEntity<>("店铺不存在", HttpStatus.NOT_FOUND);
        } else if (Objects.equals(result, "409")) {
            return new ResponseEntity<>("商品名称重复", HttpStatus.CONFLICT);
        }
        // 返回成功响应
        return ResponseEntity.ok("商品更新库存成功");
    }

    /**
     * 更新商品状态。
     *
     * @param
     * @return 如果成功，则返回 shop 实体；如果未找到对应的店铺，则返回 null。
     */
    @PostMapping("/updateStatus")
    public ResponseEntity<String> updateStatus(
            @RequestParam("id") String id,
            @RequestParam("status") String status) {
        Integer id_int = Integer.parseInt(id);
        System.out.println(status);
        String result = productService.updateStatus(id_int, status);
        if (Objects.equals(result, "404")) {
            return new ResponseEntity<>("店铺不存在", HttpStatus.NOT_FOUND);
        } else if (Objects.equals(result, "409")) {
            return new ResponseEntity<>("商品名称重复", HttpStatus.CONFLICT);
        }
        // 返回成功响应
        return ResponseEntity.ok("商品状态更新成功");
    }

    /**
     * 增加商品。
     *
     * @param
     * @return 如果成功，则返回 shop 实体；如果未找到对应的店铺，则返回 null。
     */
    @PostMapping("/addProduct")
    public ResponseEntity<String> addProduct(
            @RequestParam("name") String name,
            @RequestParam("category") String category,
            @RequestParam("price") Double price,
            @RequestParam("description") String description,
            @RequestParam("unit") String unit,
            @RequestParam("notice") String notice,
            @RequestParam("stock") String stockJson,    // Json字符串 用于接收库存信息
            @RequestParam("images") String images,     // 接受图片
            @RequestParam("shop_id") Integer shop_id) throws JsonProcessingException {
        String result = productService.createProduct(name, category, price, description, unit, notice, stockJson, images, shop_id);
        if (Objects.equals(result, "404")) {
            return new ResponseEntity<>("店铺不存在", HttpStatus.NOT_FOUND);
        } else if (Objects.equals(result, "409")) {
            return new ResponseEntity<>("商品名称重复", HttpStatus.CONFLICT);
        }
        // 返回成功响应
        return ResponseEntity.ok("商品创建成功");
    }

    /**
     * 更新商品。
     *
     * @param
     * @return 如果成功，则返回 shop 实体；如果未找到对应的店铺，则返回 null。
     */
    @PostMapping("/updateAll")
    public ResponseEntity<String> updateAll(
            @RequestParam("name") String name,
            @RequestParam("category") String category,
            @RequestParam("price") Double price,
            @RequestParam("description") String description,
            @RequestParam("unit") String unit,
            @RequestParam("notice") String notice,
            @RequestParam("quantity") String stockJson,    // Json字符串 用于接收库存信息
            @RequestParam("images") String imagesJson,     // 接受图片
            @RequestParam("product_id") Integer id) throws JsonProcessingException {
        String result = productService.updateProduct(name, category, price, description, unit, notice, stockJson, imagesJson, id);
        if (Objects.equals(result, "404")) {
            return new ResponseEntity<>("店铺不存在", HttpStatus.NOT_FOUND);
        } else if (Objects.equals(result, "409")) {
            return new ResponseEntity<>("商品名称重复", HttpStatus.CONFLICT);
        }
        // 返回成功响应
        return ResponseEntity.ok("商品更新成功");
    }
    @GetMapping("/homeview")
    public ResponseEntity<List<Product>> getHomeview(@RequestParam("uid") int uid){
        List<Product> products= null;
        try {
            products = productService.getHomeview(uid);
            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}

package com.example.demo.router;

import com.example.demo.Exception.NameException;
import com.example.demo.enums.AdvertisementStatus;
import com.example.demo.enums.ShopStatus;
import com.example.demo.pojo.Advertise;
import com.example.demo.pojo.Shop;
import com.example.demo.service.AdvertiseService;
import com.example.demo.service.PictureService;
import com.example.demo.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/*
@RestController restful方式返回前端，具体格式code标准百度查询
@RequestMapping("/advertise") 网页前缀，例如http://localhost:8081/advertise
@GetMapping("/")表示具体请求方式和路径，例如/表示http://localhost:8081/advertise/，前端使用这个url即可获取
对应的还有@GetMapping、@PostMapping、@PutMapping、@DeleteMapping
new ResponseEntity<>(HttpStatus.ok) 用于规范返回code，便于前端处理，否则全为200ok难debug，具体HttpStatus返回规范遵循restful

注意：router相当于controller，不要在这里写具体业务，这里只负责获取request并构造response，具体业务放到service，通过函数调用实现
 */

@RestController
@RequestMapping("/shop")
public class ShopRouter {

    @Autowired
    private ShopService shopService;
    @Autowired
    private PictureService pictureService;

    @GetMapping("/")
    public ResponseEntity<List<Shop>> getAllShop() {
        return new ResponseEntity<>(shopService.getAllShop(), HttpStatus.OK);
    }

    /**
     * 更新指定ID的店铺状态。
     *
     * @param id     店铺唯一标识符 (作为查询参数)
     * @param status 新的状态值 (作为查询参数)
     * @return 如果成功更新，则返回 200 OK；如果未找到对应的店铺，则返回 404 Not Found；
     *         如果更新失败，则返回 500 Internal Server Error。
     */
    @PutMapping("/status")//http://localhost:8081/shop/status?id=1&status=closed
    public ResponseEntity<Void> setShopStatus(
            @RequestParam("id") int id,
            @RequestParam("status") ShopStatus status) {
        try {
            boolean updated = shopService.setShopStatus(id, status);
            if (updated) {
                return new ResponseEntity<>(HttpStatus.OK); //200ok
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);//404 not found
            }
        } catch (Exception e) {
            // 记录异常信息到日志
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);//500 error
        }
    }

    /**
     * 查找指定ID的店铺。
     *
     * @param request     查询体
     * @return 如果成功，则返回 shop 实体；如果未找到对应的店铺，则返回 null。
     */
    @PostMapping("/getById")//http://localhost:8081/shop/getById
    public Shop getShopById(@RequestBody Map<String, Object> request) {
        Integer id = (Integer) request.get("id");
        try {
            Shop t = shopService.getShopById(id);
            if (t != null) {
                return t;
            } else {
                return null;
            }
        } catch (Exception e) {
            // 记录异常信息到日志
            System.out.println(e);
            return null;
        }
    }

    /**
     * 查找指定User_id的店铺状态。
     *
     * @param request     查询体
     * @return 如果成功，则返回 shop 实体；如果未找到对应的店铺，则返回 null。
     */
    @PostMapping("/checkStatus")
    public String checkStatus(@RequestBody Map<String, Object> request) {
        System.out.println("Check");
        Integer id = (Integer) request.get("id");
        try {
            Shop t = shopService.getShopByUserId(id);
            if (t != null) {
                if ( t.getStatus() != ShopStatus.waiting )
                    return "almost shop";
                return "waiting";
            } else {
                return "no shop";
            }
        } catch (Exception e) {
            // 记录异常信息到日志
            System.out.println(e);
            return null;
        }
    }

    // 处理店铺申请的 POST 请求
    @PostMapping("/apply")
    public ResponseEntity<String> apply(
            @RequestParam("shop_name") String shopName,
            @RequestParam("shop_description") String shopDescription,
            @RequestParam("province") String province,
            @RequestParam("pictures") MultipartFile pictures,
            @RequestParam("user_id") String user_id) {

        // 打印接收到的参数（用于调试）
//        System.out.println("店铺名称: " + shopName);
//        System.out.println("店铺描述: " + shopDescription);
//        System.out.println("特产地: " + province);
//        System.out.println(Integer.valueOf(user_id));

        Integer picture_id = null;
        // 处理文件上传
        try {
            if (!pictures.isEmpty()) {
                picture_id = pictureService.save_picture(pictures);
                System.out.println(picture_id);
                shopService.createShop(shopName, shopDescription, province, picture_id, Integer.valueOf(user_id));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("文件上传失败");
        } catch (NameException e) {
            throw new RuntimeException(e);
        }

        // 返回成功响应
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 查找指定ID的店铺。
     *
     * @param request     查询体
     * @return 如果成功，则返回 shop 实体；如果未找到对应的店铺，则返回 null。
     */
    @PostMapping("/getByUserId")//http://localhost:8081/shop/getById
    public Shop getShopByUserId(@RequestBody Map<String, Object> request) {
        Integer id = (Integer) request.get("id");
        try {
            Shop t = shopService.getShopByUserId(id);
            if (t != null) {
                return t;
            } else {
                return null;
            }
        } catch (Exception e) {
            // 记录异常信息到日志
            System.out.println(e);
            return null;
        }
    }
}

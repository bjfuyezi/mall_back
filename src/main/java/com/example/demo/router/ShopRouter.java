package com.example.demo.router;

import com.example.demo.enums.AdvertisementStatus;
import com.example.demo.enums.ShopStatus;
import com.example.demo.pojo.Advertise;
import com.example.demo.pojo.Shop;
import com.example.demo.service.AdvertiseService;
import com.example.demo.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}

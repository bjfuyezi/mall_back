package com.example.demo.router;

import com.example.demo.Exception.NameException;
import com.example.demo.enums.AdvertisementStatus;
import com.example.demo.enums.ProductStatus;
import com.example.demo.enums.ShopStatus;
import com.example.demo.pojo.Advertise;
import com.example.demo.pojo.Product;
import com.example.demo.pojo.Shop;
import com.example.demo.service.AdvertiseService;
import com.example.demo.service.PictureService;
import com.example.demo.service.ProductService;
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
@RequestMapping("/product")
public class ProductRouter {

    @Autowired
    private ProductService productService;
    @Autowired
    private PictureService pictureService;

    /**
     * 查找指定店铺ID的销量。
     *
     * @param request     查询体
     * @return 如果成功，则返回 shop 实体；如果未找到对应的店铺，则返回 null。
     */
    @PostMapping("/getSalenumByShopId")
    public ResponseEntity<Integer> getSalenumByShopId(@RequestBody Map<String, Object> request) {
        Integer id = (Integer) request.get("id");
        try {
            Integer t = productService.getSalenumByShopId(id);
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


}

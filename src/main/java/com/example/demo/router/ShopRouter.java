package com.example.demo.router;

import com.example.demo.Exception.NameException;
import com.example.demo.enums.AdvertisementStatus;
import com.example.demo.enums.ShopStatus;
import com.example.demo.enums.UserShopRelation;
import com.example.demo.pojo.Advertise;
import com.example.demo.pojo.Shop;
import com.example.demo.service.AdvertiseService;
import com.example.demo.service.PictureService;
import com.example.demo.service.ShopService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.management.relation.Relation;
import java.io.IOException;
import java.util.Date;
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

    @PostMapping("/")
    public ResponseEntity<List<Shop>> getAllShop(@RequestBody Map<String, Object> request) {
        return new ResponseEntity<>(shopService.getAllShop(), HttpStatus.OK);
    }

    /**
     * 更新指定ID的店铺状态。
     *
     * @return 如果成功更新，则返回 200 OK；如果未找到对应的店铺，则返回 404 Not Found；
     *         如果更新失败，则返回 500 Internal Server Error。
     */
    @PostMapping("/status")//http://localhost:8081/shop/status?id=1&status=closed
    public ResponseEntity<Void> setShopStatus(@RequestBody Map<String, Object> request) {
        Integer id = (Integer) request.get("id");
        ShopStatus status = ShopStatus.valueOf((String) request.get("status"));
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
     * 更新指定ID的店铺状态并写入reason。
     *
     * @return 如果成功更新，则返回 200 OK；如果未找到对应的店铺，则返回 404 Not Found；
     *         如果更新失败，则返回 500 Internal Server Error。
     */
    @PostMapping("/insertReason")//http://localhost:8081/shop/status?id=1&status=closed
    public ResponseEntity<Void> insertReason(@RequestBody Map<String, Object> request) {
        Integer id = (Integer) request.get("id");
        String reason = (String) request.get("reason");
        ShopStatus status = ShopStatus.valueOf((String) request.get("status"));
        try {
            boolean updated = shopService.insertReason(id, status, reason);
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
     * 更新商铺与用户的关系。
     *
     * @param request     查询体
     * @return 如果成功，则返回总销量；如果未找到，则返回 null。
     */
    @PostMapping("/selectByUser")
    public ResponseEntity<List<Shop>> selectByUser(@RequestBody Map<String, Object> request) {
        try {
            Integer uid = (Integer) request.get("uid");
            List<Shop> t = shopService.selectByUser(uid);
            if (t!=null) {
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
     * 更新商铺与用户的关系。
     *
     * @param request     查询体
     * @return 如果成功，则返回总销量；如果未找到，则返回 null。
     */
    @PostMapping("/changeRelation")
    public ResponseEntity<String> changeRelation(@RequestBody Map<String, Object> request) {
        try {
            Integer sid = (Integer) request.get("sid");
            Integer uid = (Integer) request.get("uid");
            String relation = (String) request.get("relation");
            String t = shopService.changeRelation(sid,uid,relation);
            if (t!=null) {
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
     * 查询商铺与用户的关系。
     *
     * @param request     查询体
     * @return 如果成功，则返回总销量；如果未找到，则返回 null。
     */
    @PostMapping("/checkRelation")
    public ResponseEntity<String> checkRelation(@RequestBody Map<String, Object> request) {
        try {
            Integer sid = (Integer) request.get("sid");
            Integer uid = (Integer) request.get("uid");
            String t = shopService.checkRelation(sid,uid);
            if (t!=null) {
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
        Integer id = (Integer) request.get("id");
        try {
            Shop t = shopService.getShopByUser_id(id);
            if (t != null) {
                if ( t.getStatus() == ShopStatus.waiting )
                    return "waiting";
                else if ( t.getStatus() == ShopStatus.closed || t.getStatus() == ShopStatus.open ) {
                    return "almost shop";
                } else {
                    String reason = t.getReason();
                    return "您的申请被打回，请参照以下原因进行修改：" + reason;
                }
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

        Integer picture_id = null;
        // 处理文件上传
        try {
            if (!pictures.isEmpty()) {
                picture_id = pictureService.save_picture(pictures);
                Integer shop_id = shopService.createShop(shopName, shopDescription, province, picture_id, Integer.valueOf(user_id));
                if ( shop_id == -1 ) {
                    return ResponseEntity.status(409).body("存在重名店铺！");
                }
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
    @PostMapping("/getByUser_id")//http://localhost:8081/shop/getById
    public Shop getShopByUser_id(@RequestBody Map<String, Object> request) {
        Integer id = (Integer) request.get("id");
        try {
            Shop t = shopService.getShopByUser_id(id);
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
     * 删除指定ID的店铺状态。
     *
     * @return 如果成功更新，则返回 200 OK；如果未找到对应的店铺，则返回 404 Not Found；
     *         如果更新失败，则返回 500 Internal Server Error。
     */
    @PostMapping("/deleteShop")
    public ResponseEntity<Void> deleteShop(@RequestBody Map<String, Object> request) {
        Integer id = (Integer) request.get("id");
        try {
            boolean updated = shopService.deleteShop(id);
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
     * 更新指定ID的店铺。
     *
     * @return 如果成功更新，则返回 200 OK；如果未找到对应的店铺，则返回 404 Not Found；
     *         如果更新失败，则返回 500 Internal Server Error。
     */
    @PostMapping("/updateShop")
    public ResponseEntity<String> updateShop(
            @RequestParam("shop_id") String shop_id,
            @RequestParam("shop_name") String shopName,
            @RequestParam("description") String description,
            @RequestParam("location") String location,
            @RequestParam("picture") MultipartFile picture) {
        Integer id = Integer.parseInt(shop_id);

        Integer picture_id = null;
        // 处理文件上传
        try {
            if (!picture.isEmpty()) {
                picture_id = pictureService.save_picture(picture);
                Shop shop = shopService.getShopById(id);
                shop.setShop_name(shopName);
                shop.setShop_description(description);
                shop.setLocation(location);
                shop.setPicture_id(picture_id);
                shop.setUpdated_time(new Date());

                String t = shopService.updateShop(shop);
                if ( t.equals("200"))
                    shopService.setShopStatus(id, ShopStatus.waiting);
                return new ResponseEntity<>(t,HttpStatus.OK);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
        } catch (NameException e) {
            throw new RuntimeException(e);
        }

        // 返回成功响应
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

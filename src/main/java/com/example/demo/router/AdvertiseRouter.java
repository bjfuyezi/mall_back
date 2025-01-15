package com.example.demo.router;

import com.example.demo.Exception.NameException;
import com.example.demo.Exception.NumException;
import com.example.demo.config.Utils;
import com.example.demo.enums.AdvertisementStatus;
import com.example.demo.enums.AdvertisementType;
import com.example.demo.pojo.Advertise;
import com.example.demo.service.AdvertiseService;
import com.example.demo.service.PictureService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/*
@RestController restful方式返回前端，具体格式code标准百度查询
@RequestMapping("/advertise") 网页前缀，例如http://localhost:8081/advertise
@GetMapping("/")表示具体请求方式和路径，例如/表示http://localhost:8081/advertise/，前端使用这个url即可获取
对应的还有@GetMapping、@PostMapping、@PutMapping、@DeleteMapping
new ResponseEntity<>(HttpStatus.ok) 用于规范返回code，便于前端处理，否则全为200ok难debug，具体HttpStatus返回规范遵循restful

注意：router相当于controller，不要在这里写具体业务，这里只负责获取request并构造response，具体业务放到service，通过函数调用实现
 */

@RestController
@RequestMapping("/advertise")
public class AdvertiseRouter {

    @Autowired
    private AdvertiseService advertiseService;
    @Autowired
    private PictureService pictureService;
    /**
     * 显示所有广告，包括已失效
     * @return 所有广告
     */
    @GetMapping("/admin")
    public ResponseEntity<List<Advertise>> getAdvertiseAll() {
        return new ResponseEntity<>(advertiseService.getAdvertiseAll(), HttpStatus.OK);
    }

    /**
     *
     * @param uid
     * @return
     */
    @GetMapping("/user")
    public ResponseEntity<List<Advertise>> getAdvertiseByStatusAndUser(@Param("uid") Integer uid) {
        return new ResponseEntity<>(advertiseService.getAdvertiseByStatusAndUser(uid), HttpStatus.OK);
    }
    /**
     * 显示在banner页的广告，要求广告状态为running
     * 若有到时间的approved则更新为running
     * @return banner的广告
     */
    @GetMapping("/banner")
    public ResponseEntity<List<Advertise>> getBannner() {
        List<Advertise> data = advertiseService.getBanners();
        if(data == null){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }else return new ResponseEntity<>(data, HttpStatus.OK);
    }

    /**
     * 申请新的广告
     */
    @PostMapping("/create")
    public ResponseEntity<Advertise> createAdvertise(
            @RequestParam("ps_id") int ps_id,
            @RequestParam("type") AdvertisementType type,
            @RequestParam("start_time") String start_time,
            @RequestParam("end_time") String end_time,
            @RequestParam("price") double price,
            @RequestParam("picture") MultipartFile picture,
            @RequestParam("banner") boolean banner,
            @RequestParam("times") int times,
            @RequestParam("name") String name,
            @RequestParam("shop_id") int shop_id){
        try {
            if(banner){
                Date start = Utils.TimetoDate(start_time,false);
                Date end = Utils.TimetoDate(end_time,false);
                if(!advertiseService.CheckBanner(start,end)){
                    return new ResponseEntity<>(HttpStatus.CONFLICT);
                }else {
                    int pic_id = pictureService.save_picture(picture);
                    Advertise advertise = advertiseService.createAdvertise(ps_id, type, start_time, end_time, price, pic_id, banner,times, name,shop_id);
                    return new ResponseEntity<>(advertise,HttpStatus.OK);
                }
            }else{
                int pic_id = pictureService.save_picture(picture);
                Advertise advertise = advertiseService.createAdvertise(ps_id, type, start_time, end_time, price, pic_id, banner, times, name,shop_id);
                return new ResponseEntity<>(advertise,HttpStatus.OK);
            }

        } catch (ParseException | NameException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }catch (NumException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     *
     * @param advertise
     * @return
     */
    @PostMapping("/status")//http://localhost:8081/advertise/status?id=1&status=running
    public ResponseEntity<Void> setAdvertiseStatus(
            @RequestBody Advertise advertise) {
        try {
            boolean updated = advertiseService.setAdvertiseStatus(advertise.getAdvertisement_id(),advertise.getStatus(),advertise.getReason());
            if (updated) {
                return new ResponseEntity<>(HttpStatus.OK); //200ok
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);//404 not found
            }
        } catch (Exception e) {
            // 记录异常信息到日志
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);//500 error
        }
    }

    @PostMapping("/checkdel")
    public ResponseEntity<Void> checkAndDelete(@RequestParam("id") int id) {
        if(advertiseService.checkAndDelete(id)){
            return new ResponseEntity<>(HttpStatus.OK); //200ok
        }else return new ResponseEntity<>(HttpStatus.NOT_FOUND); //not found
    }

    @DeleteMapping("/del")
    public ResponseEntity<List<Advertise>> deleteAdvertise(@RequestParam("id") Integer id){
        try {
            boolean updated = advertiseService.deleteAdvertise(id);
            if (updated) {
                return new ResponseEntity<>(HttpStatus.OK); //200ok
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);//404 not found
            }
        } catch (Exception e) {
            // 记录异常信息到日志
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);//500 error
        }
    }
    /**
     *
     * @return 广告的细节，给管理员看
     */
    @GetMapping("/detail")
    public ResponseEntity<Advertise> getAdvertiseDetail(@RequestParam("id") int id) {
        Advertise advertise = advertiseService.getAdvertiseDetail(id);
        if(advertise == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(advertise, HttpStatus.OK);
    }

    /**
     * 打回广告申请
     * @param reason
     * @return
     */
    @PutMapping("/reason")//http://localhost:8081/advertise/status?id=1&status=running
    public ResponseEntity<Void> RejectAdvertise(
            @RequestParam("id") int id,
            @RequestParam("reason") String reason) {
        try {
            boolean updated = advertiseService.rejectAdvertise(id, reason);
            if (updated) {
                return new ResponseEntity<>(HttpStatus.OK); //200ok
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);//404 not found
            }
        } catch (Exception e) {
            // 记录异常信息到日志
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);//500 error
        }
    }
    @GetMapping("/money")
    public ResponseEntity<String> CheckTime( @RequestParam("start") String start_time,
                                             @RequestParam("end") String end_time,
                                             @RequestParam("times") int times,
                                             @RequestParam("banner") boolean banner){
        try {
            String money = advertiseService.Cal_Money(start_time,end_time,times,banner);
            System.out.println(money);
            return new ResponseEntity<>(money,HttpStatus.OK);
        } catch (ParseException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);//500 error
        }
    }

    @PostMapping("/updateinfo")//http://localhost:8081/advertise/status?id=1&status=running
    public ResponseEntity<Void> updateAdvertise(
            @RequestBody Advertise advertise) {
        System.out.println(advertise);
        try {
            boolean updated = advertiseService.updateAdvertise(advertise.getAdvertisement_id(), advertise.getName(),advertise.getPicture_id());
            if (updated) {
                return new ResponseEntity<>(HttpStatus.OK); //200ok
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);//404 not found
            }
        } catch (Exception e) {
            // 记录异常信息到日志
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);//500 error
        }
    }
}

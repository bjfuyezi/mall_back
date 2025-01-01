package com.example.demo.router;

import com.example.demo.enums.AdvertisementStatus;
import com.example.demo.enums.AdvertisementType;
import com.example.demo.pojo.Advertise;
import com.example.demo.service.AdvertiseService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
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

    /**
     * 显示所有广告，包括已失效
     * @return 所有广告
     */
    @GetMapping("/")
    public ResponseEntity<List<Advertise>> getAdvertiseByStatus(@Param("status") AdvertisementStatus status) {
        return new ResponseEntity<>(advertiseService.getAdvertiseByStatus(status), HttpStatus.OK);
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
    public ResponseEntity<Void> createAdvertise(
            @RequestParam("ps_id") int ps_id,
            @RequestParam("type") AdvertisementType type,
            @RequestParam("start_time") String start_time,
            @RequestParam("end_time") String end_time,
            @RequestParam("price") double price,
            @RequestParam("pictrue") int pic_id,
            @RequestParam("banner") boolean banner){
        try {
            advertiseService.createAdvertise(ps_id, type, start_time, end_time, price, pic_id, banner);
        } catch (ParseException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
    /**
     * 更新指定 ID 的广告状态。
     *
     * @param id     广告的唯一标识符 (作为查询参数)
     * @param status 新的状态值 (作为查询参数)
     * @return 如果成功更新，则返回 200 OK；如果未找到对应的广告，则返回 404 Not Found；
     *         如果更新失败，则返回 500 Internal Server Error。
     */
    @PutMapping("/status")//http://localhost:8081/advertise/status?id=1&status=running
    public ResponseEntity<Void> setAdvertiseStatus(
            @RequestParam("id") int id,
            @RequestParam("status") AdvertisementStatus status) {
        try {
            boolean updated = advertiseService.setAdvertiseStatus(id, status);
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
}

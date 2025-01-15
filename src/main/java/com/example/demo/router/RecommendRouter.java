package com.example.demo.router;

import com.example.demo.pojo.Product;
import com.example.demo.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping("/recommend")
public class RecommendRouter {
    @Autowired
    RecommendService recommendService;


    @PostMapping("/history")
    public ResponseEntity<Void> insertNewHistory(@RequestBody Map<String, Object> request) {
        try {
            Integer pid = (Integer) request.get("pid");
            Integer uid = (Integer) request.get("uid");
            recommendService.insertRecommendHistory(uid,pid);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            // 记录异常信息到日志
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);//500 error
        }
    }
}

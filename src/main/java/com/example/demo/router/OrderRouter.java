package com.example.demo.router;

import com.example.demo.enums.ShopStatus;
import com.example.demo.pojo.Comment;
import com.example.demo.pojo.Orders;
import com.example.demo.service.OrderService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@RestController
@RequestMapping("/order")
public class OrderRouter {
    @Autowired
    private OrderService orderService;
    @PostMapping("/create")
    public ResponseEntity<Void> insertOrder(
            @RequestBody Orders order){
        try {
            System.out.println("Received order data: " + order);
            order.setPayment_method("微信支付");
            order.setStatus("待支付");

            LocalDateTime now = LocalDateTime.now();
            Date createtime = java.sql.Timestamp.valueOf(now);;
            order.setCreat_time(createtime);
            System.out.println(order.getOrder_id()+order.getStatus()+order.getPayment_method()+order.getPrice()
            +order.getCreat_time()+order.getQuantity());
            orderService.addOrder(order);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}

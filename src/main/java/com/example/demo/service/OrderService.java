package com.example.demo.service;

import com.example.demo.mapper.OrderMapper;
import com.example.demo.pojo.Orders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    @Autowired OrderMapper orderMapper;
    public void addOrder(Orders order) {
        orderMapper.addOrder(order);
    }
}

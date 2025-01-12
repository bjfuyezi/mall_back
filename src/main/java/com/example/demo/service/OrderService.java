package com.example.demo.service;

import com.example.demo.mapper.OrderMapper;
import com.example.demo.pojo.Orders;
import com.example.demo.pojo.Product;
import com.example.demo.pojo.Vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    @Autowired OrderMapper orderMapper;
    public void addOrder(Orders order) {
        orderMapper.addOrder(order);
    }
    public void updateStatusAndPaymentMethod(Orders order) {
        orderMapper.updateStatusAndPaymentMethod(order);
    }

    public List<OrderVo> selectAllOrderVo(int userid) {
        return orderMapper.selectAllOrderVo(userid);
    }
    public void updateStatus(Orders order) {
        orderMapper.updateStatus(order);
    }
}

package com.example.demo.service;

import com.example.demo.mapper.OrderMapper;
import com.example.demo.mapper.PictureMapper;
import com.example.demo.pojo.Orders;
import com.example.demo.pojo.Product;
import com.example.demo.pojo.Vo.OrderVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    @Autowired OrderMapper orderMapper;
    @Autowired
    PictureMapper pictureMapper;
    public void addOrder(Orders order) {
        orderMapper.addOrder(order);
    }
    public void updateStatusAndPaymentMethod(Orders order) {
        orderMapper.updateStatusAndPaymentMethod(order);
    }

    public List<OrderVo> selectAllOrderVo(int userid) throws JsonProcessingException {
        List<OrderVo> orders= orderMapper.selectAllOrderVo(userid);
        for(OrderVo orderVo:orders){
            orderVo.url=pictureMapper.selectById(Integer.parseInt(orderVo.getimgAsList().get(0))).getUrl();
        }
        return orders;
    }
    public void updateStatus(Orders order) {
        orderMapper.updateStatus(order);
    }
}

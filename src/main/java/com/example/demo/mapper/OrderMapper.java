package com.example.demo.mapper;

import com.example.demo.pojo.Orders;
import com.example.demo.pojo.Vo.OrderVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderMapper {
    List<Orders> selectAll();
    void addOrder(Orders order);
    void updateStatusAndPaymentMethod(Orders order);

    List<OrderVo> selectAllOrderVo(int userid);

    void updateStatus(Orders order);
}

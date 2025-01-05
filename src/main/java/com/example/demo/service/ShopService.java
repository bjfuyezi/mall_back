package com.example.demo.service;

import com.example.demo.enums.ShopStatus;
import com.example.demo.mapper.ShopMapper;
import com.example.demo.pojo.Shop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
/*
服务（Service）实现业务逻辑。
@Service 标记的类通常包含复杂的业务规则、事务管理以及对多个 DAO 的协调调用。
使用 @Service 注解时，Spring 的组件扫描机制会自动检测到这个类，并将其注册为 Spring 容器中的一个 Bean
可以利用依赖注入来简化对象之间的依赖关系管理：可以自动注入UserMapper，无需通过构造函数显示创建。
 */
@Service
public class ShopService {
    /*
    字段注入
     */
    @Autowired
    private ShopMapper shopMapper;
    public List<Shop> getAllShop() {
        return shopMapper.selectAll();
    }
    public Shop getShopById(int id) {
        return shopMapper.selectById(id);
    }
    public Shop getShopByUser_id(int id) {
        return shopMapper.selectByUser_id(id);
    }
    public boolean setShopStatus(int id, ShopStatus status){
        Shop shop = getShopById(id);
        if(shop == null){
            return false;
        }
        shopMapper.updateStatus(id, status);
        return true;
    }

    public boolean updateShop(Shop shop){
        Shop test = shopMapper.selectById(shop.getShop_id());
        if(test == null){
            return false;
        }
        shopMapper.updateShop(shop);
        return true;
    }

    // @Todo 需要先检查有没有未完成的订单 需要订单的查询方法
    public boolean deleteShop(int id){
        /*
        List<Order> orders = orderMapper.getOrdersByShop(id);
        for (Order order : orders) {
            if (order.getStatus() != OrderStatus.completed || order.getStatus() != OrderStatus.canceled) {
                return false;
            }
        }
        */
        shopMapper.deleteById(id);
        return true;
    }

    public boolean createShop(String name, String description, String location, Integer picture_id, Integer user_id) {
        Shop shop = new Shop();
        shop.setShop_description(description);
        shop.setShop_name(name);
        shop.setLevel(Double.valueOf(0));
        shop.setSalary(Double.valueOf(0));
        shop.setLocation(location);
        shop.setStatus(ShopStatus.waiting);
        shop.setUser_id(user_id);
        shop.setPicture_id(picture_id);
        shop.setCreated_time(new Date());
        shop.setUpdated_time(new Date());

        shopMapper.createShop(shop);
        return true;
    }
}

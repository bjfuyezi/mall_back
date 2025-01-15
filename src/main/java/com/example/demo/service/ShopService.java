package com.example.demo.service;

import com.example.demo.enums.ShopStatus;
import com.example.demo.enums.UserShopRelation;
import com.example.demo.mapper.ShopMapper;
import com.example.demo.mapper.UserShopMapper;
import com.example.demo.pojo.Shop;
import com.example.demo.pojo.UserShop;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

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
    @Autowired
    private UserShopMapper userShopMapper;
    @Autowired
    private RecommendService recommendService;

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

    public boolean insertReason(int id, ShopStatus status, String reason){
        Shop shop = getShopById(id);
        if(shop == null){
            return false;
        }
        shop.setStatus(status);
        shop.setReason(reason);
        shopMapper.updateReason(shop);
        return true;
    }

    public String updateShop(Shop shop){
        Shop check = shopMapper.selectByName(shop.getShop_name());
        Shop test = shopMapper.selectById(shop.getShop_id());
        if ( test == null ) {
            return "404";
        }
        if ( check != null && !check.getShop_id().equals(shop.getShop_id()))
            return "409";
        shopMapper.updateShop(shop);
        return "200";
    }

    public String checkRelation(Integer sid, Integer uid){
        UserShop test = userShopMapper.selectByUserAndShop(uid, sid);
        if ( test == null )
            return "none";
        if ( test.getRelation() == UserShopRelation.black){
            return "black";
        }
        return "star";
    }

    public List<Shop> selectByUser(Integer uid){
        return userShopMapper.selectByUser(uid);
    }

    public String changeRelation(Integer sid, Integer uid, String relation) throws JsonProcessingException {
        UserShop test = userShopMapper.selectByUserAndShop(uid, sid);
        if ( relation.equals("none") && test != null )
            userShopMapper.delete(uid, sid);
        else if ( test == null ) {
            UserShop userShop = new UserShop();
            userShop.setUser_id(uid);
            userShop.setShop_id(sid);
            userShop.setRelation(UserShopRelation.valueOf(relation));
            userShopMapper.insert(userShop);
            if ( relation.equals("star") )
                recommendService.insertRecommendStarShop(uid,sid);
        }
        else {
            test.setRelation(UserShopRelation.valueOf(relation));
            userShopMapper.update(test);
        }
        return "200";
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

    public Integer createShop(String name, String description, String location, Integer picture_id, Integer user_id) {
        Shop checkName = shopMapper.selectByName(name);
        if ( checkName != null && !Objects.equals(checkName.getUser_id(), user_id)) {
            return -1;
        }
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

        Shop test = shopMapper.selectByUser_id(user_id);
        if ( test != null && test.getStatus() == ShopStatus.suspended ) {
            shop.setShop_id(test.getShop_id());
            shopMapper.updateShop(shop);
            shopMapper.updateStatus(test.getShop_id(), ShopStatus.waiting);
        } else
            shopMapper.createShop(shop);
        return shopMapper.selectByUser_id(user_id).getShop_id();
    }
}

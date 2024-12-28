package com.example.demo.service;

import com.example.demo.enums.AdvertisementStatus;
import com.example.demo.mapper.AdvertiseMapper;
import com.example.demo.pojo.Advertise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
/*
服务（Service）实现业务逻辑。
@Service 标记的类通常包含复杂的业务规则、事务管理以及对多个 DAO 的协调调用。
使用 @Service 注解时，Spring 的组件扫描机制会自动检测到这个类，并将其注册为 Spring 容器中的一个 Bean
可以利用依赖注入来简化对象之间的依赖关系管理：可以自动注入UserMapper，无需通过构造函数显示创建。
 */
@Service
public class AdvertiseService {
    /*
    字段注入
     */
    @Autowired
    private AdvertiseMapper advertiseMapper;
    public List<Advertise> getAllAdvertise() {
        return advertiseMapper.selectAll();
    }
    public Advertise getAdvertiseById(int id) {
        return advertiseMapper.selectById(id);
    }
    public boolean setAdvertiseStatus(int id, AdvertisementStatus status){
        Advertise advertisement = getAdvertiseById(id);
        if(advertisement == null){
            return false;
        }
        advertiseMapper.updateStatus(id,status);
        return true;
    }
}

package com.example.demo.mapper;

import com.example.demo.enums.AdvertisementStatus;
import com.example.demo.enums.ShopStatus;
import com.example.demo.pojo.Advertise;
import com.example.demo.pojo.Shop;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
/*
@mapper 自动扫描mapper
 */
@Mapper
public interface ShopMapper {
    List<Shop> selectAll();
    Shop selectById(@Param("id") int id);
    Shop selectByUserId(@Param("id") int id);
    void updateStatus(@Param("id") int id, @Param("status") ShopStatus status);
    void deleteById(@Param("id") int id);
    void updateShop(Shop shop);
    void createShop(Shop shop);
}

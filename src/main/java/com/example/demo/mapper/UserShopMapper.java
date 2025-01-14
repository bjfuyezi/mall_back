package com.example.demo.mapper;

import com.example.demo.enums.UserShopRelation;
import com.example.demo.pojo.Shop;
import com.example.demo.pojo.UserShop;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserShopMapper {
    public List<Shop> selectByUser(@Param("uid") int uid);
    UserShop selectByUserAndShop(@Param("uid") int uid, @Param("sid") int sid);
    void insert(UserShop userShop);
    void update(UserShop userShop);
    void delete(@Param("uid") int uid, @Param("sid") int sid);
}

package com.example.demo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserShopMapper {
    public List<Integer> selectByUser(@Param("uid") int uid);
}

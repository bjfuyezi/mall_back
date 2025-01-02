package com.example.demo.mapper;

import com.example.demo.pojo.Addresses;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressesMapper {
    // 获取所有地址
    List<Addresses> selectAll();

    // 根据ID获取地址
    Addresses selectById(@Param("id") int id);

    // 根据用户ID获取地址列表
    List<Addresses> selectByUserId(@Param("userId") int userId);

    // 插入新地址
    void insert(Addresses address);

    // 更新地址信息
    void update(Addresses address);

    // 删除地址
    void deleteById(@Param("id") int id);

    // 重置用户的所有地址为非默认
    void resetDefaultAddress(@Param("userId") int userId);

    // 设置地址为默认地址
    void setDefaultAddress(@Param("addressId") int addressId);

    // 获取用户的默认地址
    Addresses getDefaultAddress(@Param("userId") int userId);
}

package com.example.demo.mapper;

import com.example.demo.pojo.Address;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressesMapper {
    @Select("SELECT * FROM addresses")
    List<Address> selectAll();

    @Select("SELECT * FROM addresses WHERE address_id = #{id}")
    Address selectById(@Param("id") int id);

    @Insert("INSERT INTO addresses (user_id, address_content, created_time, province, is_default, phone) " +
            "VALUES (#{user_id}, #{address_content}, #{created_time}, #{province}, #{is_default}, #{phone})")
    void insert(Address address);

    @Update("UPDATE addresses SET user_id = #{user_id}, address_content = #{address_content}, " +
            "province = #{province}, is_default = #{is_default}, phone = #{phone} WHERE address_id = #{address_id}")
    void update(Address address);

    @Delete("DELETE FROM addresses WHERE address_id = #{id}")
    void deleteById(@Param("id") int id);
}

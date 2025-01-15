package com.example.demo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserProductMapper {
    public List<Integer> selectByUser(@Param("uid") int uid);
    List<Integer> selectAllByProductId(@Param("id") int pid);
    void deleteStar(@Param("pid") int pid, @Param("uid") int uid);
    Integer isStar(@Param("pid") int pid, @Param("uid") int uid);
    void insert(@Param("pid") int pid, @Param("uid") int uid);
}

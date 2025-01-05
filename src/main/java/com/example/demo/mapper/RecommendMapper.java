package com.example.demo.mapper;

import com.example.demo.pojo.Recommend;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RecommendMapper {
    void insertRecommend(@Param("user_id") Integer user_id,
                         @Param("product_id") String product_id,
                         @Param("search") String search);
    Recommend selectRecommend();
}

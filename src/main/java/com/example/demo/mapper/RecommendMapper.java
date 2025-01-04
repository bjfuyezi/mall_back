package com.example.demo.mapper;

import com.example.demo.pojo.Recommend;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RecommendMapper {
    void insertRecommend(@Param("userId") Integer userId,
                         @Param("productId") String productId,
                         @Param("search") String search);
    Recommend selectRecommend();
}

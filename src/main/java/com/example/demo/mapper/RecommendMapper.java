package com.example.demo.mapper;

import com.example.demo.pojo.Recommend;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RecommendMapper {
    void insertRecommend(Recommend recommend);
    void updateRecommend(Recommend recommend);
    Recommend selectRecommend(@Param("uid") Integer uid);
}

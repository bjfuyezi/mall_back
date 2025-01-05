package com.example.demo.service;

import com.example.demo.mapper.RecommendMapper;
import com.example.demo.pojo.Recommend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendService {

    @Autowired
    private RecommendMapper recommendMapper;

    /**
     * 插入新的推荐记录
     *
     * @param user_id 用户ID
     * @param recentProduct_ids 最近访问的商品ID列表
     * @param recentSearches 最近的搜索文本列表
     */
    public void insertRecommendation(Integer user_id, List<String> recentProduct_ids, List<String> recentSearches) {
        try {
            // 创建一个新的Recommend实例
            Recommend recommend = new Recommend();
            recommend.setUser_id(user_id);
            recommend.setProduct_idFromList(recentProduct_ids);
            recommend.setSearchFromList(recentSearches);

            // 调用Mapper执行插入
            recommendMapper.insertRecommend(recommend.getUser_id(), recommend.getProduct_id(), recommend.getSearch());
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert lists to JSON or insert into database", e);
        }
    }

    public void readRecommendation() {
        try {
            // 调用Mapper执行查询
            Recommend recommend = recommendMapper.selectRecommend();
            System.out.println(recommend);
            List<String> productIdAsList= recommend.getProduct_idAsList();
            List<String> searchList = recommend.getSearchAsList();
            System.out.println(productIdAsList.size());
            System.out.println(searchList.size());
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert lists to JSON or insert into database", e);
        }
    }
}
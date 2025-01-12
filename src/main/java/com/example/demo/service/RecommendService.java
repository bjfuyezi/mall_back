package com.example.demo.service;

import com.example.demo.mapper.RecommendMapper;
import com.example.demo.pojo.Product;
import com.example.demo.pojo.Recommend;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecommendService {

    @Autowired
    private RecommendMapper recommendMapper;
    @Autowired
    private ProductService productService;
    /**
     * 插入新的推荐记录
     *
     * @param user_id 用户ID
     * @param recentProduct_ids 最近访问的商品ID列表
     * @param recentSearches 最近的搜索文本列表
     */
    public void insertRecommendation(Integer user_id, List<Integer> recentProduct_ids, List<String> recentSearches) {
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
            Recommend recommend = recommendMapper.selectRecommend(1);
            System.out.println(recommend);
            List<Integer> productIdAsList= recommend.getProduct_idAsList();
            List<String> searchList = recommend.getSearchAsList();
            System.out.println(productIdAsList.size());
            System.out.println(searchList.size());
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert lists to JSON or insert into database", e);
        }
    }

    public List<Product> Bycontent(List<Product> all, int uid) throws JsonProcessingException {
        List<Product> recommendedProducts = new ArrayList<>();
        //读取推荐表内容，获得最近20条浏览记录和最近20次搜索文本
        Recommend recommend = recommendMapper.selectRecommend(uid);
        List<Integer> recentProductIds= recommend.getProduct_idAsList();
        List<String> searchTerms = recommend.getSearchAsList();

        //最近20条浏览记录，先全部查询一遍，避免之后重复遍历查询
        List<Product> recentProducts = new ArrayList<>();
        for (Integer productId : recentProductIds) {
            //recentProducts.add(productService.selectById(productId));//查询
        }

        //开始遍历每个商品，计算每个商品的相似度
        for (Product product : all) {
            if (recentProductIds.contains(product.getProduct_id())) continue; // 排除最近点击的商品
            //开始计算权重
            double similarityScore = 0.0;
            //先遍历最近的浏览记录，根据名字和描述计算相似度，累加到similarityScore里
            for (Product p : recentProducts) {
                //similarityScore += calculateSimilarity(product.getName()+product.getDescription(), p.getName()+p.getDescription());
            }
            for (String searchTerm : searchTerms) {
                //similarityScore += calculateSimilarity(product.getName(), searchTerm);
            }
            product.setSimilarityScore(similarityScore);
            recommendedProducts.add(product);
        }

        return recommendedProducts;
    }

    public List<Product> Byuser(List<Product> all, int uid){
        List<Product> recommendedProducts = new ArrayList<>();
        return recommendedProducts;
    }
}
package com.example.demo.service;

import com.example.demo.enums.ProductStatus;
import com.example.demo.enums.ProductType;
import com.example.demo.enums.ShopStatus;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.mapper.ShopMapper;
import com.example.demo.pojo.Product;
import com.example.demo.pojo.Shop;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

/*
服务（Service）实现业务逻辑。
@Service 标记的类通常包含复杂的业务规则、事务管理以及对多个 DAO 的协调调用。
使用 @Service 注解时，Spring 的组件扫描机制会自动检测到这个类，并将其注册为 Spring 容器中的一个 Bean
可以利用依赖注入来简化对象之间的依赖关系管理：可以自动注入UserMapper，无需通过构造函数显示创建。
 */
@Service
public class ProductService {
    /*
    字段注入
     */
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ShopMapper shopMapper;
    @Autowired
    private RecommendService recommendService;

    public Integer getSalenumByShop_id(Integer id) {
        Integer sum = 0;
        List<Product> products = productMapper.selectAllProductByShop_id(id);
        for (Product product : products) {
            sum += product.getSalenum();
        }
        return sum;
    }

    public List<Product> getAllByShop_id(Integer id) {
        return productMapper.selectAllProductByShop_id(id);
    }

    public String createProduct(String name, String category, Double price, String description, String unit, String notice, String stockJson, String images, Integer shop_id) throws JsonProcessingException {
        // 处理重名
        List<Product> productList = productMapper.selectAllProductByShop_id(shop_id);
        for (Product product : productList) {
            if (product.getName().equals(name)) {
                return "409";       // 存在重名
            }
        }
        // 查找店铺
        Shop shop = shopMapper.selectById(shop_id);
        if (shop == null) {
            return "404";     // 404
        }
        // 数据转换
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> imageList = new ArrayList<>(Arrays.asList(images.replaceAll(",$", "").split(",")));
        String imageJson = objectMapper.writeValueAsString(imageList);
        Map<String, String> categoryMap = new HashMap<>();
        // 将中文类别和英文简写加入Map中
        categoryMap.put("生鲜食品", "fresh");
        categoryMap.put("零食小吃", "snack");
        categoryMap.put("酒水饮料", "drink");
        categoryMap.put("干货腌货", "dry");
        categoryMap.put("即食食品", "instant");
        categoryMap.put("农产品", "green");
        // 新建商品
        Product product = new Product();
        product.setName(name);
        product.setCategory(ProductType.valueOf(categoryMap.get(category)));
        product.setPrice(price);
        product.setDescription(description);
        product.setUnit(unit);
        product.setNotice(notice);
        product.setShop_id(shop_id);
        product.setQuantity(stockJson);
        product.setPicture_id(imageJson);
        product.setStatus(ProductStatus.waiting);
        product.setCreated_time(new Date());
        product.setUpdated_time(new Date());
        product.setLocation(shop.getLocation());
        product.setDiscount(0);
        product.setSalenum(0);
        product.setGreedy(0);

        productMapper.insert(product);

        return "200";
    }

    public List<Product> getHomeview(int uid){
        //基于用户行为的推荐
        List<Product> productall = productMapper.selectAll();
        List<Product> products_content = recommendService.Bycontent(productall,uid);
        productall.removeAll(products_content);
        List<Product> products_user = recommendService.Byuser(productall,uid);
        products_content.addAll(products_user);
        return products_content;
    }
}

package com.example.demo.mapper;

import com.example.demo.enums.ProductStatus;
import com.example.demo.pojo.Product;
import com.example.demo.pojo.Shop;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {
    void insert(Product product);
    Product selectById(@Param("id") Integer id);
    Product selectByName(@Param("name") String name);
    void updateProduct(Product product);
    void deleteById(@Param("id") int id);
    void updateStatus(Product product);
    List<Product> selectAllProductByShop_id(@Param("shop_id") int shop_id);
    List<Product> selectAll();
    List<Product> getAllProduct();
    List<Product> selectBuyHistoryByUser(@Param("uid") int uid);
    List<Product> selectAllProductBySaleLocation(@Param("location") String location);
    List<Product> selectAllProductBySaleCategory(@Param("category") String category);
    List<Product> selectAllSaleProduct();
    List<Product> selectAllEmptyProduct();
    List<Product> selectAllWaitingProduct();
    List<Product> selectByAdvertise();
    void updateReason(Product p);
    List<Integer> selectSaleByShop(@Param("shop_id") int shop_id);
    void updateGreedy(@Param("id") int id);
}

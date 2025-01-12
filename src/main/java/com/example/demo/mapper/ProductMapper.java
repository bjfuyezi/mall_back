package com.example.demo.mapper;

import com.example.demo.enums.ProductStatus;
import com.example.demo.pojo.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {
    void insert(Product product);
    Product selectById(@Param("id") int id);
    Product selectByName(@Param("name") String name);
    void updateProduct(Product product);
    void deleteById(@Param("id") int id);
    void updateStatus(@Param("id") int id, @Param("status") ProductStatus status);
    List<Product> selectAllProductByShop_id(@Param("shop_id") int shop_id);
    List<Product> selectAll();
    List<Product> selectAllProductBySaleLocation(@Param("location") String location);
    List<Product> selectAllProductBySaleCategory(@Param("category") String category);
    List<Product> selectAllSaleProduct();
    List<Product> selectAllEmptyProduct();
    List<Product> selectAllWaitingProduct();
}

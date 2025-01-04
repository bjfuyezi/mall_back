package com.example.demo.service;

import com.example.demo.enums.ProductStatus;
import com.example.demo.enums.ShopStatus;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.mapper.ShopMapper;
import com.example.demo.pojo.Product;
import com.example.demo.pojo.Shop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
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

    public Integer getSalenumByShopId(Integer id) {
        Integer sum = 0;
        List<Product> products = productMapper.selectAllProductByShopId(id);
        for (Product product : products) {
            sum += product.getSalenum();
        }
        return sum;
    }

}

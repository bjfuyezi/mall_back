package com.example.demo.mapper;

import com.example.demo.pojo.CartItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CartMapper {

    List<CartItem> getCartByUserId(int userId);

    /**
     * 获取用户的购物车商品信息，按店铺和加入时间排序
     * @param userId 用户ID
     * @return 该用户的购物车商品列表
     */
    List<CartItem> selectCartItemsByUserId(int userId);

    /**
     * 将商品添加到购物车
     * @param cartItem 购物车项
     * @return 插入的行数
     */
    int insertCartItem(CartItem cartItem);

    /**
     * 更新购物车商品的数量
     * @param userId 用户ID
     * @param productId 商品ID
     * @param quantity 新的商品数量
     * @return 更新的行数
     */
    int updateCartItemQuantity(@Param("userId") int userId, @Param("productId") int productId, @Param("quantity") int quantity);

    // 删除购物车商品项
    void deleteCartItem(@Param("userId") int userId, @Param("productId") int productId);

}

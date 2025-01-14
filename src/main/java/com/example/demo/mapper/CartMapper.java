package com.example.demo.mapper;

import com.example.demo.pojo.CartItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CartMapper {

    List<CartItem> getCartByUser_id(int user_id);

    /**
     * 获取用户的购物车商品信息，按店铺和加入时间排序
     * @param user_id 用户ID
     * @return 该用户的购物车商品列表
     */
    List<CartItem> selectCartItemsByUser_id(int user_id);

    /**
     * 将商品添加到购物车
     * @param cartItem 购物车项
     * @return 插入的行数
     */
    int insertCartItem(CartItem cartItem);

    /**
     * 更新购物车商品的数量
     * @param user_id 用户ID
     * @param product_id 商品ID
     * @param quantity 新的商品数量
     * @return 更新的行数
     */
    int updateCartItemQuantity(@Param("user_id") int user_id, @Param("product_id") int product_id, @Param("quantity") int quantity);

    // 删除购物车商品项
    void deleteCartItem(@Param("user_id") int user_id, @Param("product_id") int product_id);

    CartItem selectItemByUser_idAndProduct_id(@Param("user_id")int userId,@Param("product_id") int productId,@Param("flavor") String flavor);

    int updateCartItemQuantity(int cartItemId, int quantity);

    CartItem selectItemById(@Param("cart_item_id")int cart_item_id);

    int updateCartItemQuantity2(@Param("cart_item_id")int cartItemId,@Param("quantity")  int quantity);

    int deleteCartItemById(@Param("cart_item_id") Integer cartItemId);
}

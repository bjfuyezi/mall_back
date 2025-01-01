package com.example.demo.pojo;

import lombok.Data;

import java.util.Date;

/*
@Data自动补setter和getter，不需要再写
enum类请在enums文件夹下创建相应的enum类型，enum每一项必须和数据库里的一样
datetime 时间统一使用Date
所有的属性请用private
 */
@Data
public class CartItem {
    private Integer cartItemId;//购物车项id
    private Integer userId;//购物车所属用户id
    private Integer productId;//购物车项对应商品id
    private Integer quantity;//商品数量
    private Date addedTime;//加入购物车时间
    private Integer shopId;//店铺id

    public CartItem() {
    }

    public CartItem(Integer userId, Integer productId, Integer quantity, Integer shopId) {
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.shopId = shopId;
    }
}

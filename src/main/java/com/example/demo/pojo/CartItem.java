package com.example.demo.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private Integer cart_item_id; // 购物车项 ID
    private Integer user_id;      // 购物车所属用户 ID
    private Integer product_id;   // 购物车项对应商品 ID
    private Integer quantity;     // 商品数量
    private Date added_time;      // 加入购物车时间
    private Integer shop_id;      // 店铺 ID

    public CartItem() {
    }

    public CartItem(Integer user_id, Integer product_id, Integer quantity, Integer shop_id) {
        this.user_id = user_id;
        this.product_id = product_id;
        this.quantity = quantity;
        this.shop_id = shop_id;
    }
}

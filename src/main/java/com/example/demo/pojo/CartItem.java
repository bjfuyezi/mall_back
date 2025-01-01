package com.example.demo.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class CartItem {
    private Integer cartItemId;
    private Integer userId;
    private Integer productId;
    private Integer quantity;
    private Date addedTime;
    private Integer shopId;
}
package com.example.demo.pojo;

import lombok.Data;

@Data
public class Order {
    private int orderId;
    private int userId;
    private Integer createdTime;
    private String status; // pending, shipping, delivered 等
    private double price;
    private String paymentMethod;
    private int addressId;
    private Integer shippingTime;
    private Integer paymentTime;
    private Integer completedTime;
    private int shopId;
    private int productId;
    private int quantity;
    private String discount; // JSON 格式
}

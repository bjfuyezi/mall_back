package com.example.demo.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Orders {
    private int order_id;
    private int user_id;
    private Date created_time;
    private String status;
    private double price;
    private String payment_method= "微信支付";
    private int address_id;
    private Date pay_time;
    private Date completed_time;
    private int shop_id;
    private int product_id;
    private int quantity;


    public int getOrder_id() {
        return order_id;
    }
    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }
    public int getUser_id() {
        return user_id;
    }
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
    public Date getCreat_time() {
        return created_time;
    }

    public void setCreat_time(Date creat_time) {
        this.created_time = creat_time;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public String getPayment_method() {
        return payment_method;
    }
    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }
    public int getAddress_id() {
        return address_id;
    }
    public void setAddress_id(int address_id) {
        this.address_id = address_id;
    }
    public Date getShopping_time() {
        return completed_time;
    }
    public void setShopping_time(Date completed_time) {
        this.completed_time = completed_time;
    }
    public int getShop_id() {
        return shop_id;
    }
    public void setShop_id(int shop_id) {
        this.shop_id = shop_id;
    }
    public int getProduct_id() {
        return product_id;
    }
    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public Date getPay_time() {
        return pay_time;
    }
    public void setPay_time(Date pay_time) {
        this.pay_time = pay_time;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

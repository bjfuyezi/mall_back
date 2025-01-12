package com.example.demo.pojo.Vo;

import com.example.demo.pojo.Product;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderVo {
    public int order_id;
    public Date date;
    public String status;
    public int product_id;
    public String product_name;
    public String product_image;
    public double product_price;
//public List<Product> products;
    public int totalQuantity;
    public double totalAmount;

}

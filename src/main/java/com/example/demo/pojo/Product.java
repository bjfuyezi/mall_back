package com.example.demo.pojo;

import com.example.demo.enums.ProductStatus;
import com.example.demo.enums.ProductType;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Product {
    private Integer product_id;
    private Integer shop_id;
    private String name;
    private ProductType category;
    private double price;
    private String description;
    private double quantity;
    private List<Integer> picture_id;
    private ProductStatus status;
    private Date created_time;
    private Date updated_time;
    private String location;
    private String notice;
    private double discount;
    private String unit;
    private String flavor;
}

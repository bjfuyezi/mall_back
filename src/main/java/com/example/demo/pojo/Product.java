package com.example.demo.pojo;

import com.example.demo.enums.ProductStatus;
import com.example.demo.enums.ProductType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class Product {
    private Integer product_id;
    private Integer shop_id;
    private String name;
    private ProductType category;
    private double price;
    private String description;

    private String quantity;

    private String picture_id;

    private ProductStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date created_time;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updated_time;
    private String location;
    private String notice;
    private double discount;
    private String unit;
    private Integer salenum;
    private Integer greedy;
    private String reason;

    //数据库不存在的项
    private Double similarityScore;

    public Integer getGreedy() {
        if(greedy == null) return 0;
        return greedy;
    }
}

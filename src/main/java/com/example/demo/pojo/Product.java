package com.example.demo.pojo;

import com.example.demo.enums.ProductStatus;
import com.example.demo.enums.ProductType;
import com.example.demo.utils.JsonPicIdConverter;
import com.example.demo.utils.JsonQuantityConverter;
import jakarta.persistence.Convert;
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

    @Convert(converter = JsonQuantityConverter.class)
    private Map<String, Integer> quantity;

    @Convert(converter = JsonPicIdConverter.class)
    private List<Integer> picture_id;

    private ProductStatus status;
    private Date created_time;
    private Date updated_time;
    private String location;
    private String notice;
    private double discount;
    private String unit;
    private Integer salenum;
}

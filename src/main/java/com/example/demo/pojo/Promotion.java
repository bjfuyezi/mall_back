package com.example.demo.pojo;

import lombok.Data;

@Data
public class Promotion {
    private Integer promotion_id;
    private Integer product_id;
    private String pictures; // JSON 格式
    private String content;
}

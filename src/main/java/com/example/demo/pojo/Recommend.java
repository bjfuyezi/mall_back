package com.example.demo.pojo;

import lombok.Data;

@Data
public class Recommend {
    private Integer userId;
    private String productId; // JSON 格式
    private String search; // JSON 格式
}

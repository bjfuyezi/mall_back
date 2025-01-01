package com.example.demo.pojo;

import lombok.Data;

@Data
public class Promotion {
    private Integer promotionId;
    private Integer productId;
    private String pictures; // JSON 格式
    private String content;
}

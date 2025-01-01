package com.example.demo.pojo;

import lombok.Data;

@Data
public class UserShop {
    private Integer userId;
    private Integer shopId;
    private String relation; // black 或 star
}

package com.example.demo.pojo;

import lombok.Data;

@Data
public class UserShop {
    private Integer user_id;
    private Integer shop_id;
    private String relation; // black 或 star
}

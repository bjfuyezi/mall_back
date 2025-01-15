package com.example.demo.pojo;

import com.example.demo.enums.UserShopRelation;
import lombok.Data;

@Data
public class UserShop {
    private Integer user_id;
    private Integer shop_id;
    private UserShopRelation relation; // black 或 star
}

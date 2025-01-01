package com.example.demo.pojo;

import com.example.demo.enums.AdvertisementStatus;
import com.example.demo.enums.AdvertisementType;
import com.example.demo.enums.ShopStatus;
import lombok.Data;

import java.util.Date;

/*
@Data自动补setter和getter，不需要再写
enum类请在enums文件夹下创建相应的enum类型，enum每一项必须和数据库里的一样
datetime 时间统一使用Date
所有的属性请用private
 */
@Data
public class Shop {
    private Integer shop_id;
    private Integer user_id;
    private String shop_name;
    private String shop_description;
    private ShopStatus status;
    private Date created_time;
    private Date updated_time;
    private Double level;
    private String location;
    private Double salary;
    private Integer picture_id;
}

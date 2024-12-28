package com.example.demo.pojo;

import com.example.demo.enums.AdvertisementStatus;
import com.example.demo.enums.AdvertisementType;
import lombok.Data;

import java.util.Date;

/*
@Data自动补setter和getter，不需要再写
enum类请在enums文件夹下创建相应的enum类型，enum每一项必须和数据库里的一样
datetime 时间统一使用Date
所有的属性请用private
 */
@Data
public class Advertise {
    private int advertisement_id;
    private int shop_id;
    private int product_id;
    private AdvertisementType advertisement_type;
    private Date start_time;
    private Date end_time;
    private AdvertisementStatus status;
    private Date created_time;
    private Date updated_time;
    private String reason;
    private double price;
}
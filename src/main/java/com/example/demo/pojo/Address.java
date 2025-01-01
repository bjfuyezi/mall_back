package com.example.demo.pojo;

import lombok.Data;
import java.util.Date;
import java.util.Date;

@Data
public class Address {
    private Integer addressId;
    private Integer userId;
    private String addressContent;
    private Date createdTime;
    private String province;
    private boolean isDefault;
    private String phone;
}

package com.example.demo.pojo;

import lombok.Data;
import java.util.Date;

@Data
public class Addresses {
    private Integer addressId;
    private Integer userId;
    private String addressContent;
    private Date createdTime;
    private String province;
    private Integer isDefault;
    private String phone;
}

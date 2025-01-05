package com.example.demo.pojo;

import lombok.Data;
import java.util.Date;

@Data
public class Addresses {
    private Integer address_id;
    private Integer user_id;
    private String address_content;
    private Date create_time;
    private String province;
    private Integer is_default;
    private String phone;
}

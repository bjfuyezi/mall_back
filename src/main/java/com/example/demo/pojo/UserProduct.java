package com.example.demo.pojo;

import lombok.Data;
import java.util.Date;

@Data
public class UserProduct {
    private int user_id;
    private int product_id;
    private Date create_time;
}

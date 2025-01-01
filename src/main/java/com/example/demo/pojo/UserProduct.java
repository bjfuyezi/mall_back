package com.example.demo.pojo;

import lombok.Data;
import java.util.Date;

@Data
public class UserProduct {
    private int userId;
    private int productId;
    private Date createdTime;
}

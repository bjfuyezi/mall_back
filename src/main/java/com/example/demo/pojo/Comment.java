package com.example.demo.pojo;

import lombok.Data;
import java.util.Date;
@Data
public class Comment {
    private Integer commentId;
    private Integer orderId;
    private Integer userId;
    private String content;
    private Integer level;
    private Integer productId;
    private Integer status;
    private Date createdTime;
    private String pictureId; // 作为 JSON 存储
}

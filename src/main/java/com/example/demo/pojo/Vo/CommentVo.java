package com.example.demo.pojo.Vo;

import com.example.demo.pojo.Product;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class CommentVo {
    public int comment_id;
    public Date created_time;
    public int level;
    public String product_name;
    public String product_image;
    public String product_firimg;
    public double product_price;
    public String content;
    public String commentimg_urls;
    public List<String> imgList = new ArrayList<>();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    public List<String> getimgAsList() throws JsonProcessingException {
        return product_image == null ? new ArrayList<>() : objectMapper.readValue(product_image, List.class);
    }

    public List<String> getimgAsList2() throws JsonProcessingException {
        return commentimg_urls == null ? new ArrayList<>() : objectMapper.readValue(commentimg_urls, List.class);
    }
}

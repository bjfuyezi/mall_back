package com.example.demo.pojo.Vo;

import com.example.demo.pojo.Product;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderVo {
    public int order_id;
    public Date date;
    public String status;
    public int product_id;
    public String product_name;
    public String product_image;
    public double product_price;
    public int totalQuantity;
    public double totalAmount;
    public String url;
    public static final ObjectMapper objectMapper = new ObjectMapper();
    public List<String> getimgAsList() throws JsonProcessingException {
        return product_image == null ? new ArrayList<>() : objectMapper.readValue(product_image, List.class);
    }


    public void setimgFromList(List<String> productIds) throws JsonProcessingException {
        this.product_image = objectMapper.writeValueAsString(productIds.stream().toList());
    }

}

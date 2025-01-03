package com.example.demo.pojo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.util.List;

@Data
public class Recommend {
    private Integer userId;
    private String productId; // JSON 格式
    private String search; // JSON 格式

    private static final ObjectMapper objectMapper = new ObjectMapper();
    // Helper methods for converting between JSON string and Java object
    public List<String> getProductIdAsList() throws JsonProcessingException {
        return productId == null ? null : objectMapper.readValue(productId, List.class);
    }

    public void setProductIdFromList(List<String> productIds) throws JsonProcessingException {
        this.productId = objectMapper.writeValueAsString(productIds.stream().toList());
    }

    public List<String> getSearchAsList() throws JsonProcessingException {
        return search == null ? null : objectMapper.readValue(search, List.class);
    }

    public void setSearchFromList(List<String> searches) throws JsonProcessingException {
        this.search = objectMapper.writeValueAsString(searches.stream().limit(10).toList());
    }
}

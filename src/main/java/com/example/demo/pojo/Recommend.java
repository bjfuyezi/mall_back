package com.example.demo.pojo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.util.List;

@Data
public class Recommend {
    private Integer user_id;
    private String product_id; // JSON 格式
    private String search; // JSON 格式

    private static final ObjectMapper objectMapper = new ObjectMapper();
    // Helper methods for converting between JSON string and Java object
    public List<String> getProduct_idAsList() throws JsonProcessingException {
        return product_id == null ? null : objectMapper.readValue(product_id, List.class);
    }

    public void setProduct_idFromList(List<String> productIds) throws JsonProcessingException {
        this.product_id = objectMapper.writeValueAsString(productIds.stream().toList());
    }

    public List<String> getSearchAsList() throws JsonProcessingException {
        return search == null ? null : objectMapper.readValue(search, List.class);
    }

    public void setSearchFromList(List<String> searches) throws JsonProcessingException {
        this.search = objectMapper.writeValueAsString(searches.stream().limit(10).toList());
    }
}

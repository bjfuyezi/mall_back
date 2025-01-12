package com.example.demo.pojo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Recommend {
    private Integer user_id;
    private String history; // JSON 格式
    private String search; // JSON 格式
    private String other; // JSON 格式
    private String interest; // JSON 格式

    private static final ObjectMapper objectMapper = new ObjectMapper();
    // Helper methods for converting between JSON string and Java object
    public List<Integer> getHistoryAsList() throws JsonProcessingException {
        return history == null ? new ArrayList<>() : objectMapper.readValue(history, List.class);
    }

    public void setHistoryFromList(List<Integer> productIds) throws JsonProcessingException {
        this.history = objectMapper.writeValueAsString(productIds.stream().toList());
    }

    public List<String> getSearchAsList() throws JsonProcessingException {
        return search == null ? new ArrayList<>() : objectMapper.readValue(search, List.class);
    }

    public void setSearchFromList(List<String> searches) throws JsonProcessingException {
        this.search = objectMapper.writeValueAsString(searches.stream().limit(10).toList());
    }
    public List<Integer> getOtherAsList() throws JsonProcessingException {
        return other == null ? new ArrayList<>()  : objectMapper.readValue(other, List.class);
    }

    public void setOtherFromList(List<Integer> productIds) throws JsonProcessingException {
        this.other = objectMapper.writeValueAsString(productIds.stream().toList());
    }

    public List<Integer> getInterestAsList() throws JsonProcessingException {
        return interest == null ? new ArrayList<>() : objectMapper.readValue(interest, List.class);
    }

    public void setInterestFromList(List<Integer> productIds) throws JsonProcessingException {
        this.interest = objectMapper.writeValueAsString(productIds.stream().toList());
    }
}

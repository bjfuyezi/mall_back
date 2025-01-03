package com.example.demo.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class JsonQuantityConverter extends BaseTypeHandler<Map<String, Integer>> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Map<String, Integer> parameter, JdbcType jdbcType) throws SQLException {
        try {
            // 将 Map 转换为 JSON 字符串，并设置到 PreparedStatement
            ps.setString(i, objectMapper.writeValueAsString(parameter));
        } catch (IOException e) {
            throw new SQLException("Error converting Map to JSON", e);
        }
    }

    @Override
    public Map<String, Integer> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        try {
            // 获取 JSON 字符串并将其转换为 Map
            String json = rs.getString(columnName);
            return json != null ? objectMapper.readValue(json, Map.class) : null;
        } catch (IOException e) {
            throw new SQLException("Error converting JSON to Map", e);
        }
    }

    @Override
    public Map<String, Integer> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        try {
            // 获取 JSON 字符串并将其转换为 Map
            String json = rs.getString(columnIndex);
            return json != null ? objectMapper.readValue(json, Map.class) : null;
        } catch (IOException e) {
            throw new SQLException("Error converting JSON to Map", e);
        }
    }

    @Override
    public Map<String, Integer> getNullableResult(java.sql.CallableStatement cs, int columnIndex) throws SQLException {
        try {
            // 获取 JSON 字符串并将其转换为 Map
            String json = cs.getString(columnIndex);
            return json != null ? objectMapper.readValue(json, Map.class) : null;
        } catch (IOException e) {
            throw new SQLException("Error converting JSON to Map", e);
        }
    }
}

package com.example.demo.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class JsonPicIdConverter extends BaseTypeHandler<List<Integer>> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<Integer> parameter, JdbcType jdbcType) throws SQLException {
        try {
            // 将 Map 转换为 JSON 字符串，并设置到 PreparedStatement
            ps.setString(i, objectMapper.writeValueAsString(parameter));
        } catch (IOException e) {
            throw new SQLException("Error converting Map to JSON", e);
        }
    }

    @Override
    public List<Integer> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        try {
            // 获取 JSON 字符串并将其转换为 Map
            String json = rs.getString(columnName);
            return json != null ? objectMapper.readValue(json, List.class) : null;
        } catch (IOException e) {
            throw new SQLException("Error converting JSON to List", e);
        }
    }

    @Override
    public List<Integer> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        try {
            // 获取 JSON 字符串并将其转换为 Map
            String json = rs.getString(columnIndex);
            return json != null ? objectMapper.readValue(json, List.class) : null;
        } catch (IOException e) {
            throw new SQLException("Error converting JSON to List", e);
        }
    }

    @Override
    public List<Integer> getNullableResult(java.sql.CallableStatement cs, int columnIndex) throws SQLException {
        try {
            // 获取 JSON 字符串并将其转换为 Map
            String json = cs.getString(columnIndex);
            return json != null ? objectMapper.readValue(json, List.class) : null;
        } catch (IOException e) {
            throw new SQLException("Error converting JSON to List", e);
        }
    }
}

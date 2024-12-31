package com.example.demo.mapper;
import com.example.demo.pojo.Picture;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PictureMapper {
    void insert(Picture picture);
    Picture selectById(@Param("id") int id);

}

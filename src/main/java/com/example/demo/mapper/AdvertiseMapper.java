package com.example.demo.mapper;

import com.example.demo.enums.AdvertisementStatus;
import com.example.demo.pojo.Advertise;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
/*
@mapper 自动扫描mapper
 */
@Mapper
public interface AdvertiseMapper {
    List<Advertise> selectAll();
    List<Advertise> selectByStatus(@Param("status") AdvertisementStatus status);
    List<Advertise> selectBanners();
    Advertise selectById(@Param("id") int id);
    void updateStatus(@Param("id") int id, @Param("status") AdvertisementStatus status);
    void addAdvertise(Advertise advertise);
    void updateReason(@Param("id") int id, @Param("status") AdvertisementStatus status, @Param("reason") String reason);
}

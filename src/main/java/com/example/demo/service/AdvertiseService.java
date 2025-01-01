package com.example.demo.service;

import com.example.demo.config.Utils;
import com.example.demo.enums.AdvertisementStatus;
import com.example.demo.enums.AdvertisementType;
import com.example.demo.mapper.AdvertiseMapper;
import com.example.demo.mapper.PictureMapper;
import com.example.demo.mapper.ShopMapper;
import com.example.demo.pojo.Advertise;
import com.example.demo.pojo.Shop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/*
服务（Service）实现业务逻辑。
@Service 标记的类通常包含复杂的业务规则、事务管理以及对多个 DAO 的协调调用。
使用 @Service 注解时，Spring 的组件扫描机制会自动检测到这个类，并将其注册为 Spring 容器中的一个 Bean
可以利用依赖注入来简化对象之间的依赖关系管理：可以自动注入UserMapper，无需通过构造函数显示创建。
 */
@Service
public class AdvertiseService {
    /*
    字段注入
     */
    @Autowired
    private AdvertiseMapper advertiseMapper;
    @Autowired
    private ShopMapper shopMapper;
    @Autowired
    private PictureMapper pictureMapper;

    public List<Advertise> getAdvertiseByStatus(AdvertisementStatus status) {
        System.out.println("111111");
        if(status == null) return advertiseMapper.selectAll();
        else{
           return advertiseMapper.selectByStatus(status);
        }
    }

    public List<Advertise> getBanners() {
        List<Advertise> advertises = advertiseMapper.selectBanners();
        List<Advertise> result = new ArrayList<>();
        System.out.println(advertises.size());
        for (Advertise ad : advertises) {
            System.out.println(ad.getEnd_time().after(new Date()));
            if (ad.getStatus() == AdvertisementStatus.running) {
                if (ad.getEnd_time().after(new Date())) {
                    result.add(ad);
                } else {
                    advertiseMapper.updateStatus(ad.getAdvertisement_id(), AdvertisementStatus.expired);
                }
            } else if (ad.getStatus() == AdvertisementStatus.approved) {
                if (ad.getStart_time().before(new Date())) {
                    advertiseMapper.updateStatus(ad.getAdvertisement_id(), AdvertisementStatus.running);
                    result.add(ad);
                }
            }
        }
        if (result.size()>5) {
            return null;
        }else {
            for(Advertise r:result){
                r.setUrl(pictureMapper.selectById(r.getPicture_id()).getUrl());
            }
            return result;
        }
    }

    public void createAdvertise(int ps_id, AdvertisementType type,String start_time,String end_time,double price,
                                int pic_id,boolean banner,String name) throws ParseException {
        Advertise advertise = new Advertise();
        if(type == AdvertisementType.product){
            advertise.setProduct_id(ps_id);
            advertise.setShop_id(null);
        }else {
            //TO do :这里需要session获取商铺的id
            advertise.setShop_id(1);
            advertise.setPicture_id(null);
        }
        advertise.setAdvertisement_type(type);

        Date stime =Utils.TimetoDate(start_time);
        Date etime =Utils.TimetoDate(end_time);
        advertise.setStart_time(stime);
        advertise.setEnd_time(etime);

        advertise.setPrice(price);
        advertise.setPicture_id(pic_id);
        advertise.setBanner(banner);
        advertise.setStatus(AdvertisementStatus.pending);
        advertise.setName(name);
        Date d = new Date();
        advertise.setCreated_time(d);
        advertise.setUpdated_time(d);

        advertiseMapper.addAdvertise(advertise);
    }

    public Advertise getAdvertiseDetail(int id) {
        Advertise advertisement = advertiseMapper.selectById(id);
        if (advertisement != null){
            Shop shop = shopMapper.selectById(advertisement.getShop_id());
            advertisement.setShop_name(shop.getShop_name());
            if(advertisement.getAdvertisement_type() == AdvertisementType.product){
                advertisement.setProduct_name("123");//这里需要一个根据商品id查询商品名称
            }
            return advertisement;
        }
        return null;
    }

    public boolean setAdvertiseStatus(int id, AdvertisementStatus status){
        Advertise advertisement = advertiseMapper.selectById(id);
        if(advertisement == null){
            return false;
        }
        advertiseMapper.updateStatus(id,status);
        return true;
    }

    public boolean rejectAdvertise(int id, String reason){
        Advertise advertisement = advertiseMapper.selectById(id);
        if(advertisement == null){
            return false;
        }
        advertiseMapper.updateReason(id,AdvertisementStatus.rejected,reason);
        return true;
    }
}

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

import java.io.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;

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

    public List<Advertise> getAdvertiseAll() {
        List<Advertise> ads = advertiseMapper.selectAll();
        for(Advertise ad: ads){
            String name =shopMapper.selectById(ad.getShop_id()).getShop_name();
            ad.setShop_name(name);
            if(ad.getPicture_id() != null){
                ad.setUrl(pictureMapper.selectById(ad.getPicture_id()).getUrl());
            }
        }
        return ads;
    }

    public List<Advertise> getAdvertiseByStatusAndUser(int uid) {
        return advertiseMapper.selectAllByUser(uid);
    }

    public List<Advertise> searchByKey(String s) {
        System.out.println("search");
        if(s == null) return advertiseMapper.selectAll();
        else{
            return advertiseMapper.selectByKey(s);
        }
    }

    public boolean checkAndDelete(int id){
        Advertise advertisement = advertiseMapper.selectById(id);
        if(advertisement == null){
            return false;
        }
        if(advertisement.getStatus()!=AdvertisementStatus.unpaid){
            return false;
        }
        advertiseMapper.updateStatus(id,AdvertisementStatus.pending,null);
        return true;
    }
    public List<Advertise> getBanners() {
        List<Advertise> advertises = advertiseMapper.selectBanners();
        List<Advertise> result = new ArrayList<>();
        System.out.println(advertises.size());
        LocalDate now = Utils.convertToLocalDate(new Date());
        for (Advertise ad : advertises) {
            //System.out.println(ad.getEnd_time().after(new Date()));
            if (ad.getStatus() == AdvertisementStatus.running) {
                LocalDate end_date = Utils.convertToLocalDate(ad.getEnd_time());
                if (!end_date.isBefore(now)) {//结束时间不在现在之前则加入banner
                    result.add(ad);
                } else {//结束时间在现在之后就更新
                    advertiseMapper.updateStatus(ad.getAdvertisement_id(), AdvertisementStatus.expired,null);
                }
            } else if (ad.getStatus() == AdvertisementStatus.approved) {
                LocalDate start_date = Utils.convertToLocalDate(ad.getStart_time());
                if (!start_date.isAfter(now)) {//开始时间不在现在时间之后，证明已经需要开始了
                    advertiseMapper.updateStatus(ad.getAdvertisement_id(), AdvertisementStatus.running,null);
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

    public Advertise createAdvertise(int ps_id, AdvertisementType type,String start_time,String end_time,double price,
                                int pic_id,boolean banner,int times,String name) throws ParseException {
        Advertise advertise = new Advertise();
        Date d = new Date();
        //TO do :这里需要session获取商铺的id
        advertise.setShop_id(1);
        if(banner){
            if(type == AdvertisementType.product){
                advertise.setProduct_id(ps_id);
            }else{
                advertise.setPicture_id(null);
            }
            Date stime =Utils.TimetoDate(start_time,false);
            Date etime =Utils.TimetoDate(end_time,false);
            advertise.setStart_time(stime);
            advertise.setEnd_time(etime);
        }else{
            advertise.setProduct_id(ps_id);
            advertise.setStart_time(d);
            //这里根据psid更新商品的times
        }

        advertise.setAdvertisement_type(type);
        advertise.setPrice(price);
        advertise.setPicture_id(pic_id);
        advertise.setBanner(banner);
        advertise.setStatus(AdvertisementStatus.unpaid);
        advertise.setName(name);
        advertise.setCreated_time(d);
        advertise.setUpdated_time(d);

        advertiseMapper.addAdvertise(advertise);
        return advertise;
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

    public boolean deleteAdvertise(int id) throws IOException, ClassNotFoundException {
        Advertise advertisement = advertiseMapper.selectById(id);
        if(advertisement == null){
            return false;
        }
        advertiseMapper.deleteAdvertise(id);
        CalenderMapDel(advertisement.getStart_time(),advertisement.getEnd_time());
        return true;
    }
    public boolean setAdvertiseStatus(int id, AdvertisementStatus status,String reason){
        Advertise advertisement = advertiseMapper.selectById(id);
        if(advertisement == null){
            return false;
        }
        advertiseMapper.updateStatus(id,status,reason);
        return true;
    }
    // 将TreeMap<Calendar, Integer>转换为TreeMap<Long, Integer>
    private static TreeMap<Long, Integer> convertToLongMap(TreeMap<Calendar, Integer> dailyChanges) {
        TreeMap<Long, Integer> longMap = new TreeMap<>();
        for (Map.Entry<Calendar, Integer> entry : dailyChanges.entrySet()) {
            longMap.put(entry.getKey().getTimeInMillis(), entry.getValue());
        }
        return longMap;
    }

    public static void writeToFile(String filePath, TreeMap<Calendar, Integer> dailyChanges) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            System.out.println("写入"+filePath);
            oos.writeObject(convertToLongMap(dailyChanges));
        }
    }
    // 将TreeMap<Long, Integer>转换回TreeMap<Calendar, Integer>
    private static TreeMap<Calendar, Integer> convertToCalendarMap(TreeMap<Long, Integer> longMap) {
        TreeMap<Calendar, Integer> calendarMap = new TreeMap<>((c1, c2) -> Long.compare(c1.getTimeInMillis(), c2.getTimeInMillis()));
        for (Map.Entry<Long, Integer> entry : longMap.entrySet()) {
            Calendar cal = GregorianCalendar.getInstance();
            cal.setTimeInMillis(entry.getKey());
            calendarMap.put(cal, entry.getValue());
        }
        return calendarMap;
    }

    public static TreeMap<Calendar, Integer> readFromFile(String filePath) throws IOException, ClassNotFoundException {
        File file = new File(filePath);

        // 如果文件不存在或文件大小为0，返回空数组
        if (!file.exists() || file.length() == 0) {
            return new TreeMap<Calendar,Integer>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            @SuppressWarnings("unchecked")
            TreeMap<Long, Integer> longMap = (TreeMap<Long, Integer>) ois.readObject();
            return convertToCalendarMap(longMap);
        }
    }
    // 检查是否可以在advertiseList中添加新的Advertise对象
    public boolean CheckBanner(Date starttime, Date endtime) throws IOException, ClassNotFoundException {
        TreeMap<Calendar, Integer> dailyChanges = readFromFile("src/main/resources/static/calender.txt");
        System.out.println("dailychanges大小:"+dailyChanges.size());
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(starttime);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(endtime);
        // 更新dailyChanges以包括新广告的影响
        dailyChanges.put(calendar1, dailyChanges.getOrDefault(calendar1, 0) + 1);
        calendar2.add(Calendar.DAY_OF_MONTH, 1);//天数+1包含最后一天
        dailyChanges.put(calendar2, dailyChanges.getOrDefault(calendar2, 0) - 1);
        int currentCount = 0;

        // 只需要检查从新广告开始到结束的日期范围内每一天的广告数
        for (Calendar c = (Calendar) calendar1.clone(); !c.after(calendar2); c.add(Calendar.DAY_OF_MONTH, 1)) {
            if (dailyChanges.containsKey(c)) {
                currentCount += dailyChanges.get(c);
            }
            // 如果某天的广告数量达到或超过5，则不能添加新广告
            if (currentCount >= 5) {
                // 回滚更改
                dailyChanges.put(calendar1, dailyChanges.getOrDefault(calendar1, 0) - 1);
                dailyChanges.put(calendar2, dailyChanges.getOrDefault(calendar2, 0) + 1);
                return false;
            }
        }
        System.out.println(dailyChanges);
        writeToFile("src/main/resources/static/calender.txt",dailyChanges);
        // 如果所有日期都满足条件，则可以添加新广告
        return true;
    }

    public void CalenderMapDel(Date starttime, Date endtime) throws IOException, ClassNotFoundException {
        TreeMap<Calendar, Integer> dailyChanges = readFromFile("src/main/resources/static/calender.txt");
        System.out.println("删除时dailychanges大小:"+dailyChanges.size());
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(starttime);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(endtime);
        // 删除dailyChanges里这个广告的影响
        dailyChanges.put(calendar1, dailyChanges.getOrDefault(calendar1, 0) - 1);
        calendar2.add(Calendar.DAY_OF_MONTH, 1);//天数+1包含最后一天
        dailyChanges.put(calendar2, dailyChanges.getOrDefault(calendar2, 0) + 1);
        System.out.println(dailyChanges);
        writeToFile("src/main/resources/static/calender.txt",dailyChanges);
    }


    public boolean rejectAdvertise(int id, String reason){
        Advertise advertisement = advertiseMapper.selectById(id);
        if(advertisement == null){
            return false;
        }
        advertiseMapper.updateReason(id,AdvertisementStatus.rejected,reason);
        return true;
    }

    public String Cal_Money(String start,String end,int times,boolean banner) throws ParseException {
        if(banner){
            //banner定价
            int k =Utils.TimeDiff(start,end);
            return String.valueOf(k*1000);
        }else{
            return String.valueOf(times*10);
        }
    }

    public boolean updateAdvertise(int id, String name){
        Advertise advertisement = advertiseMapper.selectById(id);
        if(advertisement == null){
            return false;
        }
        Date d = new Date();
        advertiseMapper.updateInfo(id,name,d);
        return true;
    }
}

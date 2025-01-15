package com.example.demo.service;

import com.example.demo.enums.ProductStatus;
import com.example.demo.enums.ProductType;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.mapper.RecommendMapper;
import com.example.demo.mapper.ShopMapper;
import com.example.demo.enums.ShopStatus;
import com.example.demo.mapper.*;
import com.example.demo.pojo.*;
import com.example.demo.pojo.Recommend;
import com.example.demo.pojo.Vo.CommentVo;
import com.example.demo.pojo.Vo.CommentVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Collectors;

/*
服务（Service）实现业务逻辑。
@Service 标记的类通常包含复杂的业务规则、事务管理以及对多个 DAO 的协调调用。
使用 @Service 注解时，Spring 的组件扫描机制会自动检测到这个类，并将其注册为 Spring 容器中的一个 Bean
可以利用依赖注入来简化对象之间的依赖关系管理：可以自动注入UserMapper，无需通过构造函数显示创建。
 */
@Service
public class ProductService {
    /*
    字段注入
     */
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ShopMapper shopMapper;
    @Autowired
    private RecommendService recommendService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private RecommendMapper recommendMapper;
    @Autowired
    private UserProductMapper userProductMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private AdvertiseMapper advertiseMapper;

    public List<Product> getAllProduct() {
        return productMapper.getAllProduct();
    }

    public boolean insertReason(int id, ProductStatus status, String reason){
        Product p = productMapper.selectById(id);
        if(p == null){
            return false;
        }
        p.setStatus(status);
        p.setReason(reason);
        productMapper.updateReason(p);
        return true;
    }

    public Integer getSalenumByShop_id(Integer id) {
        Integer sum = 0;
        List<Product> products = productMapper.selectAllProductByShop_id(id);
        for (Product product : products) {
            sum += product.getSalenum();
        }
        return sum;
    }

    public Integer getStarById(Integer id) {
        List<Integer> products = new ArrayList<>();
        products = userProductMapper.selectAllByProductId(id);
        return products.size();
    }

    public Integer getCommentById(Integer id) {
        List<CommentVo> num = new ArrayList<>();
        num = commentMapper.selectProductCommentVo(id);
        return num.size();
    }

    public String deleteStar(Integer pid,Integer uid) {
        userProductMapper.deleteStar(pid, uid);
        return "200";
    }

    public String isStar(Integer pid,Integer uid) {
        Integer result = userProductMapper.isStar(pid, uid);
        if ( result != 0 )
            return "true";
        return "false";
    }

    public String changeStar(Integer pid,Integer uid) {
        Integer result = userProductMapper.isStar(pid, uid);
        if ( result != 0 ) {
            userProductMapper.deleteStar(pid, uid);
            return "200";
        }
        userProductMapper.insert(pid, uid);
        return "200";
    }

    public List<Product> getAllStarByUserId(Integer id){
        List<Integer> proList = new ArrayList<>();
        List<Product> result = new ArrayList<>();
        proList = userProductMapper.selectByUser(id);
        for (Integer i : proList) {
            Product product = productMapper.selectById(i);
            result.add(product);
         }
        return result;
    }

    public List<Product> getAllByShop_id(Integer id) {
        return productMapper.selectAllProductByShop_id(id);
    }

    public String updateStatus(Integer id, String status) {
        Product product = productMapper.selectById(id);
        product.setStatus(ProductStatus.valueOf(status));
        productMapper.updateStatus(product);
        return "200";
    }

    public String updateQuantity(Integer id, String quantityJson) {
        Product product = productMapper.selectById(id);
        product.setQuantity(quantityJson);
        productMapper.updateProduct(product);
        return "200";
    }

    public String updateProduct(String name, String category, Double price, String description, String unit, String notice, String stockJson, String imagesJson, Integer id) {
        Product product = productMapper.selectById(id);
        Product checkName = productMapper.selectByName(name);
        if ( checkName != null && !Objects.equals(checkName.getProduct_id(), id))
            return "409";
        product.setName(name);
        product.setCategory(ProductType.valueOf(category));
        product.setPrice(price);
        product.setDescription(description);
        product.setUnit(unit);
        product.setNotice(notice);
        product.setPicture_id(imagesJson);
        product.setUpdated_time(new Date());
        product.setQuantity(stockJson);
        productMapper.updateProduct(product);
        return "200";
    }

    public Product getById(Integer id) {
        return productMapper.selectById(id);
    }

    public List<Product> getAllSaleProduct() {
        return productMapper.selectAllSaleProduct();
    }

    public String deleteById(Integer id) {
        productMapper.deleteById(id);
        return "200";
    }

    public String createProduct(String name, String category, Double price, String description, String unit, String notice, String stockJson, String images, Integer shop_id) throws JsonProcessingException {
        // 处理重名
        List<Product> productList = productMapper.selectAllProductByShop_id(shop_id);
        for (Product product : productList) {
            if (product.getName().equals(name)) {
                return "409";       // 存在重名
            }
        }
        // 查找店铺
        Shop shop = shopMapper.selectById(shop_id);
        if (shop == null) {
            return "404";     // 404
        }
        // 数据转换
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> imageList = new ArrayList<>(Arrays.asList(images.replaceAll(",$", "").split(",")));
        String imageJson = objectMapper.writeValueAsString(imageList);
        Map<String, String> categoryMap = new HashMap<>();
        // 将中文类别和英文简写加入Map中
        categoryMap.put("生鲜食品", "fresh");
        categoryMap.put("零食小吃", "snack");
        categoryMap.put("酒水饮料", "drink");
        categoryMap.put("干货腌货", "dry");
        categoryMap.put("即食食品", "instant");
        categoryMap.put("农产品", "green");
        // 新建商品
        Product product = new Product();
        product.setName(name);
        product.setCategory(ProductType.valueOf(categoryMap.get(category)));
        product.setPrice(price);
        product.setDescription(description);
        product.setUnit(unit);
        product.setNotice(notice);
        product.setShop_id(shop_id);
        product.setQuantity(stockJson);
        product.setPicture_id(imageJson);
        product.setStatus(ProductStatus.waiting);
        product.setCreated_time(new Date());
        product.setUpdated_time(new Date());
        product.setLocation(shop.getLocation());
        product.setDiscount(0);
        product.setSalenum(0);
        product.setGreedy(0);

        productMapper.insert(product);

        return "200";
    }

    public List<Product> getHomeview(int uid) throws JsonProcessingException {
        List<Product> productall = productMapper.selectAll();//获取所有的售卖中商品
        List<Product> buyproducts = productMapper.selectBuyHistoryByUser(uid);//查询该用户所有购买过的商品
        buyproducts = new ArrayList<>(new HashSet<>(buyproducts));//去重
        Recommend recommend = recommendMapper.selectRecommend(uid);
        Collections.shuffle(productall);
        if(recommend == null){
            List<Product> ps =productall.stream()
                    .sorted((p1, p2) -> Double.compare(p2.getGreedy(), p1.getGreedy()))
                    .limit(40)
                    .collect(Collectors.toList());
            Recommend rec = new Recommend();
            rec.setUser_id(uid);
            recommendMapper.insertRecommend(rec);
            recommendService.insertRecommendOther(uid,ps);
            return ps;
        }

        //获取上一轮之前已经看过的商品，避免重复推荐
        List<Integer> others =recommendService.getOthersByUser(uid);

        //基于用户行为的推荐
        List<Product> products_content = recommendService.Bycontent(productall,others,buyproducts,uid,recommend);

        //排除上一个方法推荐的商品，用另一种方法再获取20种推荐的商品，避免重复需要去除之前的结果
        productall.removeAll(products_content);

        //基于用户的协同过滤
        List<Product> products_user = recommendService.Byuser(others,uid,recommend);
        products_content.addAll(products_user);
        System.out.println(products_content);
        recommendService.insertRecommendOther(uid,products_content);

        for(Product p:products_content){
            if(p.getGreedy()!=null&&p.getGreedy()>0){
                List<Advertise> a=advertiseMapper.selectByProduct(p.getProduct_id());
                if(a.size()>0)
                    p.setPicture_id(String.valueOf(a.get(0).getPicture_id()));
            }
        }
        return products_content;
    }

    public List<Product> getSearchview(int uid,String key) throws JsonProcessingException {
        List<Product> productall = productMapper.selectByKey(key);//获取所有的售卖中商品
        List<Product> buyproducts = productMapper.selectBuyHistoryByUser(uid);//查询该用户所有购买过的商品
        buyproducts = new ArrayList<>(new HashSet<>(buyproducts));//去重
        Recommend recommend = recommendMapper.selectRecommend(uid);
        Collections.shuffle(productall);
        if(recommend == null){
            List<Product> ps =productall.stream()
                    .sorted((p1, p2) -> Double.compare(p2.getGreedy(), p1.getGreedy()))
                    .limit(40)
                    .collect(Collectors.toList());
            Recommend rec = new Recommend();
            rec.setUser_id(uid);
            recommendMapper.insertRecommend(rec);
            recommendService.insertRecommendOther(uid,ps);
            recommendService.insertRecommendSearch(uid,key);
            return ps;
        }

        //获取上一轮之前已经看过的商品，避免重复推荐
        List<Integer> others =recommendService.getOthersByUser(uid);

        //基于用户行为的推荐
        List<Product> products_content = recommendService.Bycontent(productall,others,buyproducts,uid,recommend);

        //排除上一个方法推荐的商品，用另一种方法再获取20种推荐的商品，避免重复需要去除之前的结果
        productall.removeAll(products_content);

        //基于用户的协同过滤
        List<Product> products_user = recommendService.Byuser(others,uid,recommend);
        products_content.addAll(products_user);
        System.out.println(products_content);
        recommendService.insertRecommendOther(uid,products_content);
        recommendService.insertRecommendSearch(uid,key);
        return products_content;
    }

    public void updateGreedy(List<String> ids){
        for (String id : ids) {
            // 模拟获取URL的过程
            productMapper.updateGreedy(Integer.parseInt(id));
        }
    }
}

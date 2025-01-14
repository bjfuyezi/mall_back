package com.example.demo.service;

import com.example.demo.enums.ProductStatus;
import com.example.demo.mapper.CartMapper;
import com.example.demo.mapper.PictureMapper;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.mapper.ShopMapper;
import com.example.demo.pojo.CartItem;
import com.example.demo.pojo.Product;
import com.example.demo.pojo.Promotion;
import com.example.demo.pojo.Shop;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/*
服务（Service）实现业务逻辑。
@Service 标记的类通常包含复杂的业务规则、事务管理以及对多个 DAO 的协调调用。
使用 @Service 注解时，Spring 的组件扫描机制会自动检测到这个类，并将其注册为 Spring 容器中的一个 Bean
可以利用依赖注入来简化对象之间的依赖关系管理：可以自动注入UserMapper，无需通过构造函数显示创建。
 */
@Service
public class CartService {
    /*字段注入：Spring 会自动注入指定的 Bean 到服务类中。
    在这里我们注入了 CartMapper
    */
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ShopMapper shopMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private PictureMapper pictureMapper;
    /**
     * 用户进入自己的购物车：获取用户购物车中的商品，按店铺和加入时间排序。
     * @param user_id 用户ID
     * @return 该用户的购物车商品列表，按同一店铺商品的加入时间排序。
     */
    // 测试合格--浮笙
    public List<Map<String, Object>> getCartItemsByUser_id(int user_id) {
        // 获取该用户的所有购物车商品
        List<CartItem> cartItems = cartMapper.getCartByUser_id(user_id);

        // 分组：将同一店铺的商品分在一起
        // 创建一个 HashMap，用来存储按店铺分组的购物车商品
        //  是键，商品列表是值
        Map<Integer, List<CartItem>> shopGroups = new HashMap<>();
        for (CartItem item : cartItems) {// 遍历每个购物车商品 (cartItems 是查询结果，包含所有购物车商品的列表)
            shopGroups.computeIfAbsent(item.getShop_id(), k -> new ArrayList<>()).add(item);
            // 使用 computeIfAbsent 方法来确保每个店铺的商品列表都已初始化
            // 如果当前  的条目不存在，就调用传入的 k -> new ArrayList<>()创建一个新的 ArrayList<CartItem> 并放入 map,新创建的键就是传入的 item.getShop_id()
            // 如果已经存在，则返回现有的列表
        }

        // 对每个店铺内的商品按照加入时间排序（越晚越前）
        for (Map.Entry<Integer, List<CartItem>> entry : shopGroups.entrySet()) {
            // 每个 entry 代表一个店铺和该店铺内的商品列表。
            // Map.Entry 是 Map 中一个内部接口，用于表示键值对。每个 Map.Entry 对象包含一个 key 和一个 value，
            // entry.getValue() 获取当前店铺的商品列表---值
            // entry.getKey() 获取当前店铺的 ---键
            // entry.getValue().sort(Comparator.comparing(CartItem::getAddedTime).reversed()); 对商品列表按照加入时间进行排序

            // 排序逻辑：
            // 使用 Comparator.comparing(CartItem::getAddedTime) 创建一个比较器，依据 CartItem 的 added_time 字段进行排序
            // reversed() 是用来反转排序顺序的，默认情况下是升序排序（越早越前），而 reversed() 将其变为降序（越晚越前）
            entry.getValue().sort(Comparator.comparing(CartItem::getAdded_time).reversed());
        }

        // 对店铺按照该店铺内商品的最晚加入时间排序（越晚越前）
        List<Map<String, Object>> result = new ArrayList<>();
        shopGroups.entrySet()  // 获取shopGroups中的所有键值对，返回的是一个Set集合，每个元素是一个Map.Entry对象
                .stream()  // 将Set集合转换为Stream流，便于进行流式操作
                .sorted((entry1, entry2) -> {  // 对流中的元素进行排序，entry1和entry2分别表示两个Map.Entry对象
                    // 获取店铺内商品最晚加入时间
                    Date latestAddedTime1 = entry1.getValue().get(0).getAdded_time();  // 获取第一个商品的加入时间作为该店铺的最晚加入时间
                    Date latestAddedTime2 = entry2.getValue().get(0).getAdded_time();  // 获取第二个店铺的最晚加入时间
                    return latestAddedTime2.compareTo(latestAddedTime1);  // 倒序排序（最新加入的店铺排在前面）
                })
                .forEach(entry -> {  // 遍历排序后的结果，对每个Map.Entry进行处理
                    Map<String, Object> shopGroup = new HashMap<>();  // 创建一个新的Map用于存储该店铺的详细信息
                    shopGroup.put("shop_id", entry.getKey());  // 将店铺ID作为key，添加到shopGroup中
                    // 使用 shopMapper 根据  获取店铺名称
                    Shop shop = shopMapper.selectById(entry.getKey());
                    String shopName = shop.getShop_name();
                    shopGroup.put("shop_name", shopName);  // 获取该店铺的名称（假设第一个商品的店铺名就是该店铺的名称）
//                    shopGroup.put("shop_detail",shop);
                    // 接下来处理购物车项数据item
                    // 获取店铺内的商品列表
                    List<Map<String, Object>> itemDetails = new ArrayList<>();
                    for (CartItem cartItem : entry.getValue()) {
                        Map<String, Object> itemDetail = new HashMap<>();

                        // 商品相关信息（从Product表中查询）
                        Product product = productMapper.selectById(cartItem.getProduct_id());
                        String picture = product.getPicture_id();//获得图片id的json字符串
                        ObjectMapper objectMapper = new ObjectMapper();
                        try {
                            List<Integer> picture_ids = objectMapper.readValue(picture, new TypeReference<List<Integer>>() {});
//                            System.out.println(picture_ids);
                            Integer picture_id = picture_ids.get(0);//第一个id
                            String pictureUrl = pictureMapper.selectById(picture_id).getUrl();//获取url
                            String flavor = cartItem.getFlavor();//获得当前购物车商品规格
                            String name = product.getName();
                            double price = product.getPrice();
                            String quantity = product.getQuantity();
                            int stock = getQuantityByFlavor(quantity,flavor);
                            if(stock==-1){
                                throw new IllegalArgumentException("找不到该商品规格的数量");
                            }
                            itemDetail.put("cart_item_id", cartItem.getCart_item_id());
                            itemDetail.put("user_id", cartItem.getUser_id());
                            itemDetail.put("product_id", cartItem.getProduct_id());
                            itemDetail.put("picture_url", pictureUrl);
                            itemDetail.put("product_name", name);
                            itemDetail.put("flavor", flavor);
                            itemDetail.put("price", price);
                            itemDetail.put("quantity", cartItem.getQuantity());  // 初始数量
                            itemDetail.put("stock", stock);  // 库存量
//                            System.out.println(cartItem.getAdded_time());
//                            itemDetail.put("added_time", cartItem.getAdded_time());
                            itemDetail.put("selected", false);  // 是否选中,默认为false
//                            System.out.println(product.getStatus());
                            itemDetail.put("available", true);  // 是否可用,默认true，没有失效
                            if(product.getStatus() == ProductStatus.empty){
                                itemDetail.put("available", false);  // 是否失效
                            }
                            itemDetail.put("cart_item",cartItem);
//                            System.out.println(itemDetail.get("available"));
//                            System.out.println(itemDetail);
                            itemDetails.add(itemDetail);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    shopGroup.put("items", itemDetails);  // 将店铺下的商品列表添加到shopGroup中
                    result.add(shopGroup);  // 将整理好的shopGroup添加到结果列表中
                });
        return result;
    }
    private int getQuantityByFlavor(String jsonString, String targetFlavor) {
        try {
            // 创建 Jackson 的 ObjectMapper 实例
            ObjectMapper objectMapper = new ObjectMapper();

            // 将 JSON 字符串解析为 JsonNode
            JsonNode rootNode = objectMapper.readTree(jsonString);

            // 遍历 JSON 数组中的每个元素
            for (JsonNode node : rootNode) {
                // 获取每个元素的 "flavor" 和 "quantity" 字段
                String flavor = node.get("flavor").asText(); // 获取 flavor 字段
                int quantity = node.get("quantity").asInt(); // 获取 quantity 字段并解析为整数

                // 如果找到匹配的 flavor，返回对应的 quantity
                if (flavor.equals(targetFlavor)) {
                    return quantity;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // 如果未找到匹配的 flavor，返回 -1 表示未找到
    }

    /**
     * 用户加入商品到购物车：根据用户ID和商品ID将商品加入购物车。
     * @param user_id 用户ID
     * @param product_id 商品ID
     * @param quantity 商品数量
     * @param shop_id 商品所属店铺ID
     * @return 是否成功加入购物车
     */
    // 测试成功--浮笙
    // 更新测试成功--浮笙1.13
    public boolean addProductToCart(int user_id, int product_id, int quantity, int shop_id,String flavor) {
        // 先判断该用户的购物车里面有没有该商品[有口味区分]
        CartItem item = cartMapper.selectItemByUser_idAndProduct_id(user_id,product_id,flavor);
        if(item!=null){
            throw new IllegalArgumentException("该商品规格已经加入用户购物车");
        }
        // 创建新的购物车项对象
        CartItem cartItem = new CartItem(user_id,product_id,quantity,shop_id);
        cartItem.setFlavor(flavor);
        // 获取当前时间并设置为加入购物车时间
        cartItem.setAdded_time(new Date());

        // 插入购物车项到数据库
        int result = cartMapper.insertCartItem(cartItem);
        return result > 0;//看是否添加成功
    }

    /**
     * 用户改变购物车商品数量：根据购物车项ID更新购物车中的商品数量。
     * @param cart_item_id 购物车项ID
     * @param quantity 更新后的商品数量
     * @return 是否成功更新购物车商品数量
     */
    // 测试合格--浮笙
    public boolean updateCartItemQuantity(int cart_item_id, int quantity) {
        CartItem item = cartMapper.selectItemById(cart_item_id);
        if(item==null){
            throw new IllegalArgumentException("购物车项不存在");
        }
        // 根据购物车项ID更新购物车中的商品数量
        int result = cartMapper.updateCartItemQuantity2(cart_item_id, quantity);
        System.out.println(cartMapper.selectItemById(cart_item_id));
        return result > 0;
    }
    /**
     * 用户改变购物车商品数量：根据用户ID和商品ID更新购物车中的商品数量。
     * @param user_id 用户ID
     * @param product_id 商品ID
     * @param quantity 更新后的商品数量
     * @return 是否成功更新购物车商品数量
     */
//    public boolean updateCartItemQuantity(int user_id, int product_id, int quantity) {
//        // 根据用户ID和商品ID更新购物车中的商品数量
//        int result = cartMapper.updateCartItemQuantity(user_id, product_id, quantity);
//        return result > 0;
//    }

    /**
     * 删除指定购物车商品
     * @return 删除是否成功
     */
    // 测试合格--浮笙
    public boolean deleteCartItem(Integer cart_item_id) {
        CartItem item = cartMapper.selectItemById(cart_item_id);
        if(item==null){
            throw new IllegalArgumentException("购物车项不存在");
        }
        int result = cartMapper.deleteCartItemById(cart_item_id);  // 调用 CartMapper 删除购物车商品
        return result>0;
    }


    public boolean deleteCartItems(List<Integer> cartItemIds) {
        for (Integer cartItemId : cartItemIds) {
            System.out.println("正在删除购物车项的id是："+cartItemId);
            CartItem item = cartMapper.selectItemById(cartItemId);
            System.out.println("即将要删除的数据是："+item);
            if (item == null) {
                throw new IllegalArgumentException("购物车项不存在");
            }
            int result = cartMapper.deleteCartItemById(cartItemId);  // 调用 CartMapper 删除购物车商品
            if (result <= 0) {
                return false;
            }
        }
        return true;
    }
}

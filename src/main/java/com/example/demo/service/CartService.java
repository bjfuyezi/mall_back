package com.example.demo.service;

import com.example.demo.mapper.CartMapper;
import com.example.demo.mapper.ShopMapper;
import com.example.demo.pojo.CartItem;
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

    /**
     * 用户进入自己的购物车：获取用户购物车中的商品，按店铺和加入时间排序。
     * @param userId 用户ID
     * @return 该用户的购物车商品列表，按同一店铺商品的加入时间排序。
     */
    public List<Map<String, Object>> getCartItemsByUserId(int userId) {
        // 获取该用户的所有购物车商品
        List<CartItem> cartItems = cartMapper.getCartByUserId(userId);

        // 分组：将同一店铺的商品分在一起
        // 创建一个 HashMap，用来存储按店铺分组的购物车商品
        // shopId 是键，商品列表是值
        Map<Integer, List<CartItem>> shopGroups = new HashMap<>();
        for (CartItem item : cartItems) {// 遍历每个购物车商品 (cartItems 是查询结果，包含所有购物车商品的列表)
            shopGroups.computeIfAbsent(item.getShopId(), k -> new ArrayList<>()).add(item);
            // 使用 computeIfAbsent 方法来确保每个店铺的商品列表都已初始化
            // 如果当前 shopId 的条目不存在，就调用传入的 k -> new ArrayList<>()创建一个新的 ArrayList<CartItem> 并放入 map,新创建的键就是传入的 item.getShopId()
            // 如果已经存在，则返回现有的列表
        }

        // 对每个店铺内的商品按照加入时间排序（越晚越前）
        for (Map.Entry<Integer, List<CartItem>> entry : shopGroups.entrySet()) {
            // 每个 entry 代表一个店铺和该店铺内的商品列表。
            // Map.Entry 是 Map 中一个内部接口，用于表示键值对。每个 Map.Entry 对象包含一个 key 和一个 value，
            // entry.getValue() 获取当前店铺的商品列表---值
            // entry.getKey() 获取当前店铺的 shopId---键
            // entry.getValue().sort(Comparator.comparing(CartItem::getAddedTime).reversed()); 对商品列表按照加入时间进行排序

            // 排序逻辑：
            // 使用 Comparator.comparing(CartItem::getAddedTime) 创建一个比较器，依据 CartItem 的 addedTime 字段进行排序
            // reversed() 是用来反转排序顺序的，默认情况下是升序排序（越早越前），而 reversed() 将其变为降序（越晚越前）
            entry.getValue().sort(Comparator.comparing(CartItem::getAddedTime).reversed());
        }

        // 对店铺按照该店铺内商品的最晚加入时间排序（越晚越前）
        List<Map<String, Object>> result = new ArrayList<>();
        shopGroups.entrySet()  // 获取shopGroups中的所有键值对，返回的是一个Set集合，每个元素是一个Map.Entry对象
                .stream()  // 将Set集合转换为Stream流，便于进行流式操作
                .sorted((entry1, entry2) -> {  // 对流中的元素进行排序，entry1和entry2分别表示两个Map.Entry对象
                    // 获取店铺内商品最晚加入时间
                    Date latestAddedTime1 = entry1.getValue().get(0).getAddedTime();  // 获取第一个商品的加入时间作为该店铺的最晚加入时间
                    Date latestAddedTime2 = entry2.getValue().get(0).getAddedTime();  // 获取第二个店铺的最晚加入时间
                    return latestAddedTime2.compareTo(latestAddedTime1);  // 倒序排序（最新加入的店铺排在前面）
                })
                .forEach(entry -> {  // 遍历排序后的结果，对每个Map.Entry进行处理
                    Map<String, Object> shopGroup = new HashMap<>();  // 创建一个新的Map用于存储该店铺的详细信息
                    shopGroup.put("shopId", entry.getKey());  // 将店铺ID作为key，添加到shopGroup中
                    // 使用 shopMapper 根据 shopId 获取店铺名称
                    String shopName = shopMapper.selectById(entry.getKey()).getShop_name();
                    shopGroup.put("shopName", shopName);  // 获取该店铺的名称（假设第一个商品的店铺名就是该店铺的名称）
                    shopGroup.put("items", entry.getValue());  // 将店铺下的商品列表添加到shopGroup中
                    result.add(shopGroup);  // 将整理好的shopGroup添加到结果列表中
                });

        return result;
    }

    /**
     * 用户加入商品到购物车：根据用户ID和商品ID将商品加入购物车。
     * @param userId 用户ID
     * @param productId 商品ID
     * @param quantity 商品数量
     * @param shopId 商品所属店铺ID
     * @return 是否成功加入购物车
     */
    public boolean addProductToCart(int userId, int productId, int quantity, int shopId) {
        // 创建新的购物车项对象
        CartItem cartItem = new CartItem(userId,productId,quantity,shopId);

        // 获取当前时间并设置为加入购物车时间
        cartItem.setAddedTime(new Date());

        // 插入购物车项到数据库
        int result = cartMapper.insertCartItem(cartItem);
        return result > 0;//看是否添加成功
    }


    /**
     * 用户改变购物车商品数量：根据用户ID和商品ID更新购物车中的商品数量。
     * @param userId 用户ID
     * @param productId 商品ID
     * @param quantity 更新后的商品数量
     * @return 是否成功更新购物车商品数量
     */
    public boolean updateCartItemQuantity(int userId, int productId, int quantity) {
        // 根据用户ID和商品ID更新购物车中的商品数量
        int result = cartMapper.updateCartItemQuantity(userId, productId, quantity);
        return result > 0;
    }

    /**
     * 删除指定购物车商品
     *
     * @param userId 用户 ID
     * @param productId 商品 ID
     * @return 删除是否成功
     */
    public boolean deleteCartItem(int userId, int productId) {
        try {
            cartMapper.deleteCartItem(userId, productId);  // 调用 CartMapper 删除购物车商品
            return true;  // 如果删除成功，返回 true
        } catch (Exception e) {
            // 捕获异常，返回 false 表示删除失败
            e.printStackTrace();
            return false;
        }
    }
}
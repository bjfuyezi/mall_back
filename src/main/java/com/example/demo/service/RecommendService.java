package com.example.demo.service;

import com.example.demo.mapper.*;
import com.example.demo.pojo.Product;
import com.example.demo.pojo.Recommend;
import com.example.demo.pojo.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendService {

    @Autowired
    private RecommendMapper recommendMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private UserMapper userMapper;
    /**
     * 插入新的推荐记录
     *
     * @param user_id 用户ID
     * @param recentProduct_ids 最近访问的商品ID列表
     * @param recentSearches 最近的搜索文本列表
     */
    public void insertRecommendation(Integer user_id, List<Integer> recentProduct_ids, List<String> recentSearches) {
        try {
            // 创建一个新的Recommend实例
            Recommend recommend = new Recommend();
            recommend.setUser_id(user_id);
            recommend.setHistoryFromList(recentProduct_ids);
            recommend.setSearchFromList(recentSearches);

            // 调用Mapper执行插入
            recommendMapper.insertRecommend(recommend);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert lists to JSON or insert into database", e);
        }
    }

    //这个用于加入购物车或收藏商品的时候调用
    public void insertRecommendInterest(Integer user_id,Integer product_id) throws JsonProcessingException {
        Recommend recommend = recommendMapper.selectRecommend(user_id);
        if(recommend==null){
            Recommend r = new Recommend();
            r.setUser_id(user_id);
            List<Integer> interest = new ArrayList<>();
            interest.add(product_id);
            r.setInterestFromList(interest);
            // 调用Mapper执行插入
            recommendMapper.insertRecommend(r);
        }else{
            List<Integer> interest = recommend.getInterestAsList();
            interest.add(product_id);
            recommend.setInterestFromList(interest);
            recommendMapper.updateRecommend(recommend);
        }
    }

    //点击查看详情的时候调用
    public void insertRecommendHistory(Integer user_id,Integer product_id) throws JsonProcessingException {
        Recommend recommend = recommendMapper.selectRecommend(user_id);
        if(recommend==null){
            Recommend r = new Recommend();
            r.setUser_id(user_id);
            List<Integer> history = new ArrayList<>();
            history.add(product_id);
            r.setHistoryFromList(history);
            // 调用Mapper执行插入
            recommendMapper.insertRecommend(r);
        }else{
            List<Integer> history = recommend.getHistoryAsList();
            //每次删除最后一个，新的加入开头
            history.add(0,product_id);
            if(history.size()>20){
                history.remove(history.size()-1);
            }
            recommend.setHistoryFromList(history);
            recommendMapper.updateRecommend(recommend);
        }
    }

    //该函数用于更新other字段
    public void insertRecommendOther(Integer user_id,List<Product> products) throws JsonProcessingException {
        Recommend recommend = recommendMapper.selectRecommend(user_id);
        List<Integer> other = new ArrayList<>();
        for(Product p:products){
            other.add(p.getProduct_id());
        }
        if(recommend==null){
            Recommend r = new Recommend();
            r.setUser_id(user_id);
            r.setOtherFromList(other);
            // 调用Mapper执行插入
            recommendMapper.insertRecommend(r);
        }else{
            recommend.setOtherFromList(other);
            recommendMapper.updateRecommend(recommend);
        }
    }

    //获取用户排除的内容
    public List<Integer> getOthersByUser(Integer user_id) throws JsonProcessingException {
        Recommend recommend = recommendMapper.selectRecommend(user_id);
        if(recommend == null) return new ArrayList<>();
        else return recommend.getOtherAsList();
    }

    //搜索的时候调用
    public void insertRecommendSearch(Integer user_id,String key) throws JsonProcessingException {
        Recommend recommend = recommendMapper.selectRecommend(user_id);
        if(recommend==null){
            Recommend r = new Recommend();
            r.setUser_id(user_id);
            List<String> search = new ArrayList<>();
            search.add(key);
            r.setSearchFromList(search);
            // 调用Mapper执行插入
            recommendMapper.insertRecommend(r);
        }else{
            List<String> search = recommend.getSearchAsList();
            //每次删除最后一个，新的加入开头
            search.add(0,key);
            if(search.size()>10){
                search.remove(search.size()-1);
            }
            recommend.setSearchFromList(search);
            recommendMapper.updateRecommend(recommend);
        }
    }

    //收藏商铺的时候调用
    public void insertRecommendStarShop(Integer user_id,int shop_id) throws JsonProcessingException {
        Recommend recommend = recommendMapper.selectRecommend(user_id);
        List<Integer> products = productMapper.selectSaleByShop(shop_id);
        if(recommend==null){
            Recommend r = new Recommend();
            r.setUser_id(user_id);
            r.setInterestFromList(products);
            // 调用Mapper执行插入
            recommendMapper.insertRecommend(r);
        }else{
            List<Integer> interest = recommend.getInterestAsList();
            interest.addAll(products);
            recommend.setInterestFromList(interest);
            recommendMapper.updateRecommend(recommend);
        }
    }

    public void readRecommendation() {
        try {
            // 调用Mapper执行查询
            Recommend recommend = recommendMapper.selectRecommend(1);
            System.out.println(recommend);
            List<Integer> productIdAsList= recommend.getHistoryAsList();
            List<String> searchList = recommend.getSearchAsList();
            System.out.println(productIdAsList.size());
            System.out.println(searchList.size());
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert lists to JSON or insert into database", e);
        }
    }

    public List<Product> Bycontent(List<Product> all, List<Integer> others, List<Product> history, int uid,Recommend recommend) throws JsonProcessingException {
        List<Product> recommendedProducts = new ArrayList<>();
        //读取推荐表内容，获得最近20条浏览记录和最近20次搜索文本
        List<Integer> recentProductIds= recommend.getHistoryAsList();
        List<String> searchTerms = recommend.getSearchAsList();
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
        //计算最感兴趣的三个地区以及对应的要加的权值
        System.out.println(history);
        Map<String, Double> interestLocation =cal_locationrecommend(history);

        //最近20条浏览记录，先全部查询一遍，避免之后重复遍历查询
        List<Product> recentProducts = new ArrayList<>();
        for (Integer productId : recentProductIds) {
            recentProducts.add(productMapper.selectById(productId));//查询
        }

        //开始遍历每个商品，计算每个商品的相似度
        for (Product product : all) {
            if (recentProductIds.contains(product.getProduct_id())) continue; // 排除最近点击的商品
            if(others.contains(product.getProduct_id())) continue;//排除上一次返回过的商品
            //开始计算权重
            double similarityScore = 0.0;
            String text1 = product.getName()+product.getDescription();
            //先遍历最近的浏览记录，根据名字和描述计算相似度，累加到similarityScore里
            for (Product p : recentProducts) {
                //与浏览记录计算相似度  商品名称+商品描述，计算相似度
                String text2 = p.getName()+p.getDescription();
                double similarity = levenshteinDistance.apply(text1, text2);
                System.out.println("Similarity: "+product.getProduct_id()+"---"+p.getProduct_id()+"::"+ (1 - similarity / Math.max(text1.length(), text2.length())));
                similarityScore += (1 - similarity / Math.max(text1.length(), text2.length()));
            }
            for (String searchTerm : searchTerms) {
                //商品名称与搜索记录计算相似度
                double similarity = levenshteinDistance.apply(product.getName(), searchTerm);
                System.out.println("Search:" +product.getProduct_id()+"---"+searchTerm+"::"+ (1 - similarity / Math.max(product.getName().length(), searchTerm.length())));
                similarityScore += (1 - similarity / Math.max(product.getName().length(), searchTerm.length()));
            }
            //省份权重
            System.out.println(product.getLocation()+"  "+interestLocation.getOrDefault(product.getLocation(),0.0));
            similarityScore += interestLocation.getOrDefault(product.getLocation(),0.0);

            //推荐的商品额外加0.2的权重
            if(product.getGreedy()!=null&&product.getGreedy()!=0){
                similarityScore+=0.2;
            }
            System.out.println("Score:"+similarityScore);
            //记录每个商品的最终权重
            product.setSimilarityScore(similarityScore);
            recommendedProducts.add(product);
        }

        // 按照加权分数排序，留下前20个推荐商品
        return recommendedProducts.stream()
                .sorted((p1, p2) -> Double.compare(p2.getSimilarityScore(), p1.getSimilarityScore()))
                .limit(20)
                .collect(Collectors.toList());
    }

    public Map<String, Double> cal_locationrecommend(List<Product> history){
        //计算购买记录中最常购买的三个省份
        Map<String, Integer> originCountMap = new HashMap<>();

        // 统计每个产地的购买次数
        for (Product product : history) {
            String origin = product.getLocation();
            originCountMap.put(origin, originCountMap.getOrDefault(origin, 0) + 1);
        }
        // 对产地按购买次数降序排序
        List<Map.Entry<String, Integer>> sortedOrigins = originCountMap.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .collect(Collectors.toList());

        // 获取前三排名的产地进行加权
        Map<String, Double> originWeights = new HashMap<>();
        for (int i = 0; i < sortedOrigins.size() && i < 3; i++) { // 最多取前三名
            switch (i) {
                case 0:
                    originWeights.put(sortedOrigins.get(i).getKey(), 0.2);
                    break;
                case 1:
                    originWeights.put(sortedOrigins.get(i).getKey(), 0.1);
                    break;
                case 2:
                    originWeights.put(sortedOrigins.get(i).getKey(), 0.05);
                    break;
            }
        }
        return originWeights;
    }

    //当一个用户A需要个性化推荐时，可以先找到他有相似兴趣的其他用户，然后把那些用户喜欢的、而用户A没听过的物品推荐给A。
    public List<Product> Byuser(List<Integer> others,int uid,Recommend recommend) throws JsonProcessingException {
        //由于商品数>用户数，以遍历用户表为选择,建立用户兴趣list，包括用户收藏的商品，加入购物车的商品和收藏店铺中的所有商品，即用户A：商品1，商品2，商品3
        //第一步：获取当前用户的收藏商品，收藏店铺的商品，加入购物车的商品的集合u
        List<Product> recommendedProducts = new ArrayList<>();
        List<Integer> target = recommend.getInterestAsList();
        Set<Integer> u=new HashSet<>(target);//去重

        //第二步：遍历其他买家用户，获取其收藏商品，收藏店铺的商品，加入购物车的商品的集合v，计算u和v之间的余弦相似度，并保存在user中
        //u对u为0，没收藏和购买过的买家余弦相似度也默认为0
        List<User> users = userMapper.selectAllUsers();
        for(User user:users){
            if(user.getUser_id() == uid) {
                user.setCosineSimilarity(0);
                continue;
            }
            Recommend r = recommendMapper.selectRecommend(user.getUser_id());
            if(r==null||r.getInterestAsList().size()==0) {
                user.setCosineSimilarity(0);
                continue;
            }
            List<Integer> interest = r.getInterestAsList();
            Set<Integer> v=new HashSet<>(interest);//去重
            user.setCosineSimilarity(cosineSimilarity(u,v));
            user.setRecommend(v);
        }

        //第三步：根据当前用户与其他用户之间的相似度进行排名
        users =users.stream()
                .sorted((u1, u2) -> Double.compare(u2.getCosineSimilarity(), u1.getCosineSimilarity()))
                .collect(Collectors.toList());

        //第四步：从最相似到最不相似遍历，相似度为0则不考虑，得到相似的其他用户购买了，但当前用户没购买的商品合集
        //到达20个则退出
        Set<Integer> result =new HashSet<>();
        for(User user: users){
            if(user.getCosineSimilarity()==0) break;
            u.retainAll(user.getRecommend());//求差集，实际上求用户u访问过而v没有访问过的内容
            u.retainAll(others);//去掉others里的商品，也就是上一轮推荐过的商品
            result.addAll(u);//其余放进result
            if(u.size()>20) break;
        }

        //第五步：获取商品的完整信息，若筛选得到的商品不足20个，则优先用广告来补充，加入广告补充后打乱顺序
        for(int pid : result){
            Product product = productMapper.selectById(pid);
            recommendedProducts.add(product);
        }

        //不足让有曝光量的来补充
        if(recommendedProducts.size()<20){
            List<Product> products =productMapper.selectByAdvertise();
            Collections.shuffle(products);//打乱顺序
            recommendedProducts.addAll(products);
        }
        //若补充后还是小于20直接返回
        if(recommendedProducts.size()<20){//还是小于20
            return recommendedProducts;
        }else return recommendedProducts.subList(0,20);//取前20个
    }
    public double cosineSimilarity(Set<Integer> u, Set<Integer> v) {
        //计算公式是w=|N(v)&N(u)|/sqrt(|N(v)||N(u)|)
        //分子部分，求交集
        Set<Integer> intersection = new HashSet<>(u);
        intersection.retainAll(v);
        int intersectionSize = intersection.size();

        //分母部分，求长度乘积
        double denominator = Math.sqrt(u.size() * v.size());
        if (denominator == 0) {//分母不为0
            return 0;
        }
        return (double) intersectionSize/ denominator;
    }
}
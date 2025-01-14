package com.example.demo.pojo;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 支付宝配置类
 */
@Data
@Component
public class AlipayTemplate {
    /**
     * APPID
     */
    @Value("${alipay.appId}")
    private String appId;

    /**
     * 应用私钥，就是工具生成的应用私钥
     */
    @Value("${alipay.appPrivateKey}")
    public String merchantPrivateKey;

    /**
     * 支付宝公钥,对应APPID下的支付宝公钥
     */
    @Value("${alipay.alipayPublicKey}")
    public String alipayPublicKey;

    /**
     * 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
     */
    @Value("${alipay.notifyUrl}")
    public String notifyUrl;

    /**
     * 同步通知，支付成功，一般跳转到成功页
     */
    @Value("${alipay.returnUrl1}")
    public String returnUrl1;
    @Value("${alipay.returnUrl2}")
    public String returnUrl2;

    /**
     * 签名方式
     */
    @Value("${alipay.signType}")
    private String signType;

    /**
     * 字符编码格式
     */
    @Value("${alipay.charset}")
    private String charset;

    /**
     * 订单超时时间
     */
    private String timeout = "30s";

    /**
     * 支付宝网关；https://openapi-sandbox.dl.alipaydev.com/gateway.do
     */
    @Value("${alipay.gatewayUrl}")
    public String gatewayUrl;

    private static final String FORMAT = "JSON";
    /*
    调用这个方法付款广告
     */
    public String pay(int id,double price,boolean flag) throws AlipayApiException {
        System.out.println("APPID:" + appId);
        System.out.println("应用私钥:" + merchantPrivateKey);
        System.out.println("支付宝公钥:" + alipayPublicKey);
        System.out.println("支付成功的信息:" + notifyUrl);
        if(flag)
            System.out.println("同步通知，支付成功，一般跳转到成功页:" + returnUrl2);
        else
            System.out.println("同步通知，支付成功，一般跳转到成功页:" + returnUrl1);
        System.out.println("签名方式:" + signType);
        System.out.println("字符编码格式:" + charset);
        System.out.println("订单超时时间:" + timeout);
        System.out.println("支付宝网关:" + gatewayUrl);

        //注意这里，为了保证后端的订单号一致，广告的订单开头以00000，普通下单购买的订单以11111开头，五个！
        Long idl;
        if(flag){
            idl = Long.valueOf("11111"+id);
        }else {
            idl = Long.valueOf("22222"+id);
        }
        Double money = price;                    // 获取支付金额

        // 1. 根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(
                gatewayUrl,appId,
               merchantPrivateKey, FORMAT, charset, alipayPublicKey, signType);

        // 2. 创建 Request并设置Request参数
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();  // 发送请求的 Request类
        request.setNotifyUrl(notifyUrl);
        if(flag){
            request.setReturnUrl(returnUrl2);
        }else request.setReturnUrl(returnUrl1);



        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", String.valueOf(idl));  // 我们自己生成的订单编号
        bizContent.put("total_amount", String.valueOf(money)); // 订单的总金额
        bizContent.put("subject", String.valueOf(idl));   // 支付的名称用和订单号一样的
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");  // 固定配置
        request.setBizContent(bizContent.toString());

        // 执行支付请求并获取支付宝的响应
        String result = alipayClient.pageExecute(request).getBody();
        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应：" + result);
        //返回支付宝响应的结果
        return result;
    }

}
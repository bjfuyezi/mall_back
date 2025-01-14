package com.example.demo.router;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.easysdk.factory.Factory;
import com.example.demo.enums.AdvertisementStatus;
import com.example.demo.pojo.Advertise;
import com.example.demo.pojo.AlipayTemplate;
import com.example.demo.service.AdvertiseService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付宝接口
 */
@RestController
@RequestMapping("/alipay")
public class AlipayRouter {

    @Resource
    AlipayTemplate alipayTemplate;
    @Value("${alipay.alipayPub}")
    private String alipayPublicKey;
    @Autowired
    private AdvertiseService advertiseService;

    //调用这个函数传入你的id和price,如果是订单传入true，广告传入false
    @GetMapping(value = "/pay", produces = "text/html")
    @ResponseBody
    public String pay(@RequestParam int id,@RequestParam double price,@RequestParam boolean flag) throws AlipayApiException, IOException {

        // 调用支付宝支付模板进行支付
        String form= alipayTemplate.pay(id,price,flag);
        return form;
    }


    @PostMapping("/notify")  // 注意这里必须是POST接口
    public String payNotify(HttpServletRequest request) throws Exception {
        // 检查交易状态是否为成功
        if (!"TRADE_SUCCESS".equals(request.getParameter("trade_status"))) {
            System.out.println("交易失败");
            //关闭页面不是触发失败的条件，这条可以暂时不用管
            return "failure"; // 如果状态不是成功，则返回失败
        }

        System.out.println("=========支付宝异步回调========");
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            params.put(name, request.getParameter(name));
            System.out.println(name + " = " + request.getParameter(name));
        }
        String tradeNo = params.get("out_trade_no");
        String gmtPayment = params.get("gmt_payment");
        String alipayTradeNo = params.get("trade_no");
        String sign = params.get("sign");
        params.remove("sign_type");

        boolean checkSignature =AlipaySignature.rsaCheckV2(params,alipayPublicKey,"UTF-8","RSA2");
//        String content = AlipaySignature.getSignCheckContentV1(params);
//        boolean checkSignature = AlipaySignature.rsa256CheckContent(content, sign, alipayPublicKey, "UTF-8"); // 验证签名
        // 支付宝验签
        if (checkSignature) {
            // 验签通过
            System.out.println("验证通过");
            System.out.println("交易名称: " + params.get("subject"));
            System.out.println("交易状态: " + params.get("trade_status"));
            System.out.println("支付宝交易凭证号: " + params.get("trade_no"));
            System.out.println("商户订单号: " + params.get("out_trade_no"));
            System.out.println("交易金额: " + params.get("total_amount"));
            System.out.println("买家在支付宝唯一id: " + params.get("buyer_id"));
            System.out.println("买家付款时间: " + params.get("gmt_payment"));
            System.out.println("买家付款金额: " + params.get("buyer_pay_amount"));

            // 更新订单状态，可以更改order的状态
            System.out.println("更改状态");
            if(tradeNo.startsWith("22222")){
                advertiseService.setAdvertiseStatus(Integer.parseInt(tradeNo.substring(5,tradeNo.length())), AdvertisementStatus.pending,null);
                System.out.println("广告订单");
            }else if(tradeNo.startsWith("11111")){
                System.out.println("购物订单");
            }else {
                System.out.println(tradeNo);
            }

//            ordersMapper.updateState(tradeNo, "已支付", gmtPayment, alipayTradeNo);
        }else System.out.println("验证失败");

        return "success"; // 返回成功响应给支付宝
    }


}
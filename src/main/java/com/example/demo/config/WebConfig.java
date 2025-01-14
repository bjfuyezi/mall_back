package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 允许所有域名访问该接口
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080") // 允许跨域访问的来源
                .allowedMethods("GET", "POST", "DELETE", "PUT") // 允许的 HTTP 方法
                .allowedHeaders("Content-Type", "Authorization") // 允许的请求头
                .allowCredentials(true) // 是否允许携带认证信息（cookie）
                .maxAge(3600); // 预检请求缓存时间，单位为秒
    }


}

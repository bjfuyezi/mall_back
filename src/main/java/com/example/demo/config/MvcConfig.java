package com.example.demo.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String fileUploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 添加对新文件夹的支持
        registry.addResourceHandler("/img/**")
                .addResourceLocations("file:" + fileUploadDir);
    }
}
package com.example.demo.config;

import com.example.demo.utils.JsonQuantityConverter;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyBatisConfig {

    @Bean
    public SqlSessionFactory sqlSessionFactory(javax.sql.DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);

        // 注册 TypeHandler
        factoryBean.setTypeHandlersPackage("com.example.demo.utils");

        return factoryBean.getObject();
    }
}

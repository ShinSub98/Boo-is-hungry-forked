package com.chaeshin.boo.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    private String driverClassName;
    private String url;
    private String username;
    private String password;

    public DataSourceConfig(@Value("${spring.datasource.driver-class-name}") String driverClassName,
                            @Value("${spring.datasource.url}") String url,
                            @Value("${spring.datasource.username}") String username,
                            @Value("${spring.datasource.password}") String password) {
        this.driverClassName = driverClassName;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Bean
    public DataSource getDataSource() {
        return DataSourceBuilder.create()
                .driverClassName(driverClassName)
                .url(url).username(username)
                .password(password).build();
    }
}

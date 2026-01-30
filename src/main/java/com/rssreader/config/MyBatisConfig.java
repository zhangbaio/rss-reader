package com.rssreader.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.rssreader.mapper")
public class MyBatisConfig {
}

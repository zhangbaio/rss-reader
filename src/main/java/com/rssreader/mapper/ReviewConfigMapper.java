package com.rssreader.mapper;

import com.rssreader.entity.ReviewConfig;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReviewConfigMapper {

    /**
     * 获取复习配置（只有一条记录）
     */
    ReviewConfig findConfig();

    /**
     * 插入配置
     */
    int insert(ReviewConfig config);

    /**
     * 更新配置
     */
    int update(ReviewConfig config);
}

package com.rssreader.mapper;

import com.rssreader.entity.ArticleContent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ArticleContentMapper {

    ArticleContent findByArticleId(@Param("articleId") Long articleId);

    int insert(ArticleContent content);

    int update(ArticleContent content);

    int deleteByArticleId(@Param("articleId") Long articleId);
}

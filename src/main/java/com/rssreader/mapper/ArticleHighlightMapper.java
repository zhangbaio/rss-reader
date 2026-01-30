package com.rssreader.mapper;

import com.rssreader.entity.ArticleHighlight;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ArticleHighlightMapper {

    List<ArticleHighlight> findByArticleId(@Param("articleId") Long articleId);

    ArticleHighlight findById(@Param("id") Long id);

    int insert(ArticleHighlight highlight);

    int update(ArticleHighlight highlight);

    int deleteById(@Param("id") Long id);

    int deleteByArticleId(@Param("articleId") Long articleId);
}

package com.rssreader.mapper;

import com.rssreader.entity.ArticleNote;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ArticleNoteMapper {

    ArticleNote findByArticleId(@Param("articleId") Long articleId);

    int insert(ArticleNote note);

    int update(ArticleNote note);

    int deleteByArticleId(@Param("articleId") Long articleId);
}

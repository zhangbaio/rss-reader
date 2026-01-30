package com.rssreader.mapper;

import com.rssreader.dto.WebArticleQueryDTO;
import com.rssreader.entity.WebArticle;
import com.rssreader.vo.WebArticleVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WebArticleMapper {

    int insert(WebArticle webArticle);

    int updateById(WebArticle webArticle);

    int deleteById(@Param("id") Long id);

    WebArticle selectById(@Param("id") Long id);

    WebArticleVO selectVOById(@Param("id") Long id);

    List<WebArticleVO> selectList(WebArticleQueryDTO query);

    int selectCount(WebArticleQueryDTO query);

    WebArticle selectByUrl(@Param("url") String url);

    int updateCategory(@Param("id") Long id, @Param("category") String category);

    int updateReadStatus(@Param("id") Long id, @Param("isRead") Integer isRead);

    int updateFavoriteStatus(@Param("id") Long id, @Param("isFavorite") Integer isFavorite);

    int updateProgress(@Param("id") Long id, @Param("progress") Integer progress);

    int selectCountByCategory(@Param("category") String category);
}

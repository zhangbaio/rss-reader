package com.rssreader.mapper;

import com.rssreader.dto.ArticleQueryDTO;
import com.rssreader.entity.Article;
import com.rssreader.vo.ArticleVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ArticleMapper {

    int insert(Article article);

    int batchInsert(@Param("articles") List<Article> articles);

    int updateById(Article article);

    int deleteById(@Param("id") Long id);

    Article selectById(@Param("id") Long id);

    ArticleVO selectVOById(@Param("id") Long id);

    List<ArticleVO> selectList(ArticleQueryDTO query);

    int selectCount(ArticleQueryDTO query);

    List<String> selectExistingGuids(@Param("feedId") Long feedId, @Param("guids") List<String> guids);

    int updateReadStatus(@Param("id") Long id, @Param("isRead") Integer isRead);

    int batchUpdateReadStatus(@Param("ids") List<Long> ids, @Param("isRead") Integer isRead);

    int updateFavoriteStatus(@Param("id") Long id, @Param("isFavorite") Integer isFavorite);

    int selectUnreadCount(@Param("feedId") Long feedId);

    int selectUnreadCountByFeed(@Param("feedId") Long feedId);
}

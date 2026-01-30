package com.rssreader.mapper;

import com.rssreader.entity.ArticleReviewFrequency;
import com.rssreader.vo.ArticleFrequencyVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleReviewFrequencyMapper {

    /**
     * 获取有高亮的文章列表及其频率设置
     */
    List<ArticleFrequencyVO> findArticlesWithHighlights(@Param("keyword") String keyword);

    /**
     * 根据文章ID获取频率设置
     */
    ArticleReviewFrequency findByArticleId(@Param("articleId") Long articleId);

    /**
     * 插入频率设置
     */
    int insert(ArticleReviewFrequency frequency);

    /**
     * 更新频率设置
     */
    int update(ArticleReviewFrequency frequency);

    /**
     * 批量更新频率设置
     */
    int batchUpdateFrequency(@Param("articleIds") List<Long> articleIds, @Param("frequency") Integer frequency);
}

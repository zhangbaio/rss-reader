package com.rssreader.service;

import com.rssreader.dto.ArticleFrequencyDTO;
import com.rssreader.dto.ReviewConfigDTO;
import com.rssreader.vo.ArticleFrequencyVO;
import com.rssreader.vo.ReviewConfigVO;
import com.rssreader.vo.ReviewHighlightVO;
import com.rssreader.vo.ReviewStatsVO;

import java.util.List;

public interface IReviewService {

    /**
     * 获取今日复习列表
     */
    List<ReviewHighlightVO> getDailyReviewList();

    /**
     * 记录复习操作
     */
    void recordReviewAction(Long highlightId, String action);

    /**
     * 获取复习统计
     */
    ReviewStatsVO getReviewStats();

    /**
     * 获取复习配置
     */
    ReviewConfigVO getReviewConfig();

    /**
     * 更新复习配置
     */
    ReviewConfigVO updateReviewConfig(ReviewConfigDTO dto);

    /**
     * 获取有高亮的文章列表及其频率设置
     */
    List<ArticleFrequencyVO> getArticlesWithFrequency(String keyword);

    /**
     * 更新文章复习频率
     */
    void updateArticleFrequency(ArticleFrequencyDTO dto);

    /**
     * 批量更新文章复习频率
     */
    void batchUpdateArticleFrequency(List<Long> articleIds, Integer frequency);
}

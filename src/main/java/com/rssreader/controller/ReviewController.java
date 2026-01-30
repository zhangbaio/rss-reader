package com.rssreader.controller;

import com.rssreader.common.Result;
import com.rssreader.dto.ArticleFrequencyDTO;
import com.rssreader.dto.ReviewActionDTO;
import com.rssreader.dto.ReviewConfigDTO;
import com.rssreader.service.IReviewService;
import com.rssreader.vo.ArticleFrequencyVO;
import com.rssreader.vo.ReviewConfigVO;
import com.rssreader.vo.ReviewHighlightVO;
import com.rssreader.vo.ReviewStatsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/review")
public class ReviewController {

    @Autowired
    private IReviewService reviewService;

    /**
     * 获取今日复习列表
     */
    @GetMapping("/daily")
    public Result<List<ReviewHighlightVO>> getDailyReviewList() {
        List<ReviewHighlightVO> list = reviewService.getDailyReviewList();
        return Result.success(list);
    }

    /**
     * 记录复习操作
     */
    @PostMapping("/{highlightId}/action")
    public Result<String> recordReviewAction(
            @PathVariable Long highlightId,
            @RequestBody ReviewActionDTO dto) {
        reviewService.recordReviewAction(highlightId, dto.getAction());
        return Result.success("Action recorded");
    }

    /**
     * 获取复习统计
     */
    @GetMapping("/stats")
    public Result<ReviewStatsVO> getReviewStats() {
        ReviewStatsVO stats = reviewService.getReviewStats();
        return Result.success(stats);
    }

    /**
     * 获取复习配置
     */
    @GetMapping("/config")
    public Result<ReviewConfigVO> getReviewConfig() {
        ReviewConfigVO config = reviewService.getReviewConfig();
        return Result.success(config);
    }

    /**
     * 更新复习配置
     */
    @PutMapping("/config")
    public Result<ReviewConfigVO> updateReviewConfig(@RequestBody ReviewConfigDTO dto) {
        ReviewConfigVO config = reviewService.updateReviewConfig(dto);
        return Result.success(config);
    }

    /**
     * 获取有高亮的文章列表及其频率设置
     */
    @GetMapping("/articles/frequency")
    public Result<List<ArticleFrequencyVO>> getArticlesWithFrequency(
            @RequestParam(required = false) String keyword) {
        List<ArticleFrequencyVO> articles = reviewService.getArticlesWithFrequency(keyword);
        return Result.success(articles);
    }

    /**
     * 更新单篇文章的复习频率
     */
    @PutMapping("/articles/{articleId}/frequency")
    public Result<String> updateArticleFrequency(
            @PathVariable Long articleId,
            @RequestBody ArticleFrequencyDTO dto) {
        dto.setArticleId(articleId);
        reviewService.updateArticleFrequency(dto);
        return Result.success("Frequency updated");
    }

    /**
     * 批量更新文章复习频率
     */
    @PutMapping("/articles/frequency/batch")
    public Result<String> batchUpdateArticleFrequency(
            @RequestParam List<Long> articleIds,
            @RequestParam Integer frequency) {
        reviewService.batchUpdateArticleFrequency(articleIds, frequency);
        return Result.success("Frequencies updated");
    }
}

package com.rssreader.controller;

import com.rssreader.common.PageResult;
import com.rssreader.common.Result;
import com.rssreader.service.IArticleService;
import com.rssreader.util.ContentExtractor;
import com.rssreader.vo.ArticleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.rssreader.dto.ArticleQueryDTO;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    @Autowired
    private IArticleService articleService;

    // 原文内容缓存（简单实现，生产环境建议使用Redis）
    private final Map<Long, String> fullContentCache = new ConcurrentHashMap<>();

    @GetMapping
    public Result<PageResult<ArticleVO>> listArticles(ArticleQueryDTO query) {
        PageResult<ArticleVO> result = articleService.listArticles(query);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<ArticleVO> getArticleDetail(@PathVariable Long id) {
        ArticleVO article = articleService.getArticleDetail(id);
        return Result.success(article);
    }

    @PutMapping("/{id}/read")
    public Result<String> markAsRead(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer isRead) {
        articleService.markAsRead(id, isRead);
        return Result.success("Article read status updated");
    }

    @PostMapping("/batch-read")
    public Result<String> batchMarkAsRead(
            @RequestBody List<Long> ids,
            @RequestParam(defaultValue = "1") Integer isRead) {
        articleService.batchMarkAsRead(ids, isRead);
        return Result.success("Articles read status updated");
    }

    @PutMapping("/{id}/favorite")
    public Result<String> toggleFavorite(@PathVariable Long id) {
        articleService.toggleFavorite(id);
        return Result.success("Article favorite status toggled");
    }

    @GetMapping("/unread-count")
    public Result<Integer> getUnreadCount(@RequestParam(required = false) Long feedId) {
        Integer count = articleService.getUnreadCount(feedId);
        return Result.success(count);
    }

    /**
     * 获取文章原文内容
     * 从原始链接抓取完整内容
     */
    @GetMapping("/{id}/fullcontent")
    public Result<String> getFullContent(@PathVariable Long id) {
        // 先检查缓存
        if (fullContentCache.containsKey(id)) {
            return Result.success(fullContentCache.get(id));
        }

        // 获取文章信息
        ArticleVO article = articleService.getArticleDetail(id);
        if (article == null) {
            return Result.error("Article not found");
        }

        String link = article.getLink();
        if (link == null || link.isEmpty()) {
            return Result.error("Article link is empty");
        }

        // 抓取原文内容
        String content = ContentExtractor.extractFromUrl(link);
        if (content == null || content.isEmpty()) {
            return Result.error("Failed to extract content from URL");
        }

        // 缓存结果（限制缓存大小）
        if (fullContentCache.size() > 1000) {
            fullContentCache.clear();
        }
        fullContentCache.put(id, content);

        return Result.success(content);
    }

    /**
     * 清除原文缓存
     */
    @DeleteMapping("/{id}/fullcontent/cache")
    public Result<String> clearFullContentCache(@PathVariable Long id) {
        fullContentCache.remove(id);
        return Result.success("Cache cleared");
    }
}

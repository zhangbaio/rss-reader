package com.rssreader.controller;

import com.rssreader.common.PageResult;
import com.rssreader.common.Result;
import com.rssreader.dto.WebArticleDTO;
import com.rssreader.dto.WebArticleQueryDTO;
import com.rssreader.service.IWebArticleService;
import com.rssreader.vo.WebArticleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/web-articles")
public class WebArticleController {

    @Autowired
    private IWebArticleService webArticleService;

    /**
     * Add article from URL
     */
    @PostMapping
    public Result<WebArticleVO> addFromUrl(@RequestBody WebArticleDTO dto) {
        WebArticleVO article = webArticleService.addFromUrl(dto);
        return Result.success(article);
    }

    /**
     * Get article by ID
     */
    @GetMapping("/{id}")
    public Result<WebArticleVO> getById(@PathVariable Long id) {
        WebArticleVO article = webArticleService.getById(id);
        return Result.success(article);
    }

    /**
     * Get article list
     */
    @GetMapping
    public Result<PageResult<WebArticleVO>> list(WebArticleQueryDTO query) {
        PageResult<WebArticleVO> result = webArticleService.list(query);
        return Result.success(result);
    }

    /**
     * Update article category
     */
    @PutMapping("/{id}/category")
    public Result<Void> updateCategory(@PathVariable Long id, @RequestParam String category) {
        webArticleService.updateCategory(id, category);
        return Result.success();
    }

    /**
     * Mark article as read/unread
     */
    @PutMapping("/{id}/read")
    public Result<Void> markAsRead(@PathVariable Long id, @RequestParam Integer isRead) {
        webArticleService.markAsRead(id, isRead);
        return Result.success();
    }

    /**
     * Toggle favorite status
     */
    @PutMapping("/{id}/favorite")
    public Result<Void> toggleFavorite(@PathVariable Long id) {
        webArticleService.toggleFavorite(id);
        return Result.success();
    }

    /**
     * Update reading progress
     */
    @PutMapping("/{id}/progress")
    public Result<Void> updateProgress(@PathVariable Long id, @RequestParam Integer progress) {
        webArticleService.updateProgress(id, progress);
        return Result.success();
    }

    /**
     * Delete article
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        webArticleService.delete(id);
        return Result.success();
    }

    /**
     * Get category counts
     */
    @GetMapping("/counts")
    public Result<Map<String, Integer>> getCategoryCounts() {
        Map<String, Integer> counts = webArticleService.getCategoryCounts();
        return Result.success(counts);
    }
}

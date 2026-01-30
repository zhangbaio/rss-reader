package com.rssreader.service;

import com.rssreader.common.PageResult;
import com.rssreader.dto.WebArticleDTO;
import com.rssreader.dto.WebArticleQueryDTO;
import com.rssreader.vo.WebArticleVO;

import java.util.Map;

public interface IWebArticleService {

    /**
     * Add article from URL
     */
    WebArticleVO addFromUrl(WebArticleDTO dto);

    /**
     * Get article by ID
     */
    WebArticleVO getById(Long id);

    /**
     * Get article list with pagination
     */
    PageResult<WebArticleVO> list(WebArticleQueryDTO query);

    /**
     * Update article category (inbox, later, archive)
     */
    void updateCategory(Long id, String category);

    /**
     * Mark article as read/unread
     */
    void markAsRead(Long id, Integer isRead);

    /**
     * Toggle favorite status
     */
    void toggleFavorite(Long id);

    /**
     * Update reading progress
     */
    void updateProgress(Long id, Integer progress);

    /**
     * Delete article
     */
    void delete(Long id);

    /**
     * Get counts by category
     */
    Map<String, Integer> getCategoryCounts();
}

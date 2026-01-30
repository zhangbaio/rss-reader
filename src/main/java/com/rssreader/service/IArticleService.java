package com.rssreader.service;

import com.rssreader.common.PageResult;
import com.rssreader.vo.ArticleVO;
import com.rssreader.dto.ArticleQueryDTO;
import java.util.List;

public interface IArticleService {

    PageResult<ArticleVO> listArticles(ArticleQueryDTO query);

    ArticleVO getArticleDetail(Long id);

    void markAsRead(Long id, Integer isRead);

    void batchMarkAsRead(List<Long> ids, Integer isRead);

    void toggleFavorite(Long id);

    Integer getUnreadCount(Long feedId);
}

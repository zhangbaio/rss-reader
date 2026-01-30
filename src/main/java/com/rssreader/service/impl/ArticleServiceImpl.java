package com.rssreader.service.impl;

import com.rssreader.common.Constants;
import com.rssreader.common.PageResult;
import com.rssreader.dto.ArticleQueryDTO;
import com.rssreader.entity.Article;
import com.rssreader.exception.RssReaderException;
import com.rssreader.mapper.ArticleMapper;
import com.rssreader.service.IArticleService;
import com.rssreader.vo.ArticleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ArticleServiceImpl implements IArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public PageResult<ArticleVO> listArticles(ArticleQueryDTO query) {
        if (query.getPage() == null || query.getPage() < 1) {
            query.setPage(Constants.DEFAULT_PAGE);
        }
        if (query.getSize() == null || query.getSize() < 1) {
            query.setSize(Constants.DEFAULT_SIZE);
        }

        List<ArticleVO> articles = articleMapper.selectList(query);
        Integer total = articleMapper.selectCount(query);

        return new PageResult<>(query.getPage(), query.getSize(), total, articles);
    }

    @Override
    public ArticleVO getArticleDetail(Long id) {
        ArticleVO article = articleMapper.selectVOById(id);
        if (article == null) {
            throw new RssReaderException("Article not found");
        }
        return article;
    }

    @Override
    @Transactional
    public void markAsRead(Long id, Integer isRead) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new RssReaderException("Article not found");
        }
        articleMapper.updateReadStatus(id, isRead);
    }

    @Override
    @Transactional
    public void batchMarkAsRead(List<Long> ids, Integer isRead) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        articleMapper.batchUpdateReadStatus(ids, isRead);
    }

    @Override
    @Transactional
    public void toggleFavorite(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new RssReaderException("Article not found");
        }

        Integer newStatus = article.getIsFavorite().equals(Constants.IS_FAVORITE)
                ? Constants.NOT_FAVORITE
                : Constants.IS_FAVORITE;
        articleMapper.updateFavoriteStatus(id, newStatus);
    }

    @Override
    public Integer getUnreadCount(Long feedId) {
        return articleMapper.selectUnreadCount(feedId);
    }
}

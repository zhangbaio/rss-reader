package com.rssreader.service.impl;

import com.rssreader.entity.Article;
import com.rssreader.entity.ArticleContent;
import com.rssreader.mapper.ArticleContentMapper;
import com.rssreader.mapper.ArticleMapper;
import com.rssreader.service.IArticleContentService;
import com.rssreader.util.ContentExtractor;
import com.rssreader.vo.ArticleContentVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ArticleContentServiceImpl implements IArticleContentService {

    @Autowired
    private ArticleContentMapper contentMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public ArticleContentVO getContentByArticleId(Long articleId) {
        ArticleContent content = contentMapper.findByArticleId(articleId);
        return toVO(content);
    }

    @Override
    public ArticleContentVO fetchAndSaveContent(Long articleId) {
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new RuntimeException("Article not found");
        }

        ArticleContent existing = contentMapper.findByArticleId(articleId);

        // 如果已经抓取成功，直接返回
        if (existing != null && existing.getFetchStatus() == 1) {
            return toVO(existing);
        }

        // 抓取原文
        String htmlContent = null;
        int fetchStatus = 2; // 默认失败

        try {
            htmlContent = ContentExtractor.extractFromUrl(article.getLink());
            if (htmlContent != null && !htmlContent.isEmpty()) {
                fetchStatus = 1; // 成功
            }
        } catch (Exception e) {
            // 抓取失败
        }

        // 如果抓取失败，使用RSS描述
        if (htmlContent == null || htmlContent.isEmpty()) {
            htmlContent = article.getDescription();
        }

        // 计算纯文本和字数
        String plainText = htmlContent != null ? htmlContent.replaceAll("<[^>]*>", "") : "";
        int wordCount = plainText.length();

        if (existing != null) {
            existing.setContent(htmlContent);
            existing.setPlainText(plainText);
            existing.setWordCount(wordCount);
            existing.setFetchStatus(fetchStatus);
            existing.setFetchTime(new Date());
            contentMapper.update(existing);
            return toVO(contentMapper.findByArticleId(articleId));
        } else {
            ArticleContent content = new ArticleContent();
            content.setArticleId(articleId);
            content.setContent(htmlContent);
            content.setPlainText(plainText);
            content.setWordCount(wordCount);
            content.setFetchStatus(fetchStatus);
            content.setFetchTime(new Date());
            contentMapper.insert(content);
            return toVO(content);
        }
    }

    @Override
    public void saveContent(Long articleId, String content) {
        ArticleContent existing = contentMapper.findByArticleId(articleId);

        String plainText = content != null ? content.replaceAll("<[^>]*>", "") : "";
        int wordCount = plainText.length();

        if (existing != null) {
            existing.setContent(content);
            existing.setPlainText(plainText);
            existing.setWordCount(wordCount);
            existing.setFetchStatus(1);
            existing.setFetchTime(new Date());
            contentMapper.update(existing);
        } else {
            ArticleContent articleContent = new ArticleContent();
            articleContent.setArticleId(articleId);
            articleContent.setContent(content);
            articleContent.setPlainText(plainText);
            articleContent.setWordCount(wordCount);
            articleContent.setFetchStatus(1);
            articleContent.setFetchTime(new Date());
            contentMapper.insert(articleContent);
        }
    }

    private ArticleContentVO toVO(ArticleContent content) {
        if (content == null) return null;

        ArticleContentVO vo = new ArticleContentVO();
        BeanUtils.copyProperties(content, vo);
        return vo;
    }
}

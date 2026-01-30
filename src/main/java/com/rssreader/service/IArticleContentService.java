package com.rssreader.service;

import com.rssreader.vo.ArticleContentVO;

public interface IArticleContentService {

    /**
     * 获取文章原文内容
     */
    ArticleContentVO getContentByArticleId(Long articleId);

    /**
     * 抓取并保存文章原文内容
     */
    ArticleContentVO fetchAndSaveContent(Long articleId);

    /**
     * 保存文章内容
     */
    void saveContent(Long articleId, String content);
}

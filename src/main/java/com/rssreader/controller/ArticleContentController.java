package com.rssreader.controller;

import com.rssreader.common.Result;
import com.rssreader.service.IArticleContentService;
import com.rssreader.vo.ArticleContentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/article-content")
public class ArticleContentController {

    @Autowired
    private IArticleContentService contentService;

    /**
     * 获取文章原文内容
     */
    @GetMapping("/{articleId}")
    public Result<ArticleContentVO> getContent(@PathVariable Long articleId) {
        ArticleContentVO content = contentService.getContentByArticleId(articleId);
        if (content == null) {
            // 如果没有，尝试抓取
            content = contentService.fetchAndSaveContent(articleId);
        }
        return Result.success(content);
    }

    /**
     * 重新抓取文章原文
     */
    @PostMapping("/{articleId}/fetch")
    public Result<ArticleContentVO> fetchContent(@PathVariable Long articleId) {
        ArticleContentVO content = contentService.fetchAndSaveContent(articleId);
        return Result.success(content);
    }
}

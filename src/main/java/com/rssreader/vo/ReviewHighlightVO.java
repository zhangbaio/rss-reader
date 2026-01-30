package com.rssreader.vo;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class ReviewHighlightVO {
    private Long id;
    private Long articleId;
    private String selectedText;
    private Integer startOffset;
    private Integer endOffset;
    private String xpath;
    private String color;
    private String note;
    private List<TagVO> tags;
    private Date createTime;

    // 文章信息
    private ArticleInfoVO article;

    // 复习相关
    private Integer reviewCount;
    private Date lastReviewTime;
    private Integer masteryLevel; // 0: 新的, 1: 学习中, 2: 已掌握

    @Data
    public static class ArticleInfoVO {
        private Long id;
        private String title;
        private String link;
        private String feedName;
        private String feedImageUrl;
    }
}

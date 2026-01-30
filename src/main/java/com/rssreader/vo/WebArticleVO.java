package com.rssreader.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * VO for web article
 */
@Data
public class WebArticleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String url;
    private String title;
    private String description;
    private String content;
    private String author;
    private String domain;
    private String imageUrl;
    private Integer wordCount;
    private Integer readingTime;
    private Integer progress;
    private String category;
    private String language;
    private Integer isRead;
    private Integer isFavorite;
    private Date savedTime;
    private Date createTime;
    private Integer highlightCount;
}

package com.rssreader.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Web Article entity - for URL-based articles (not from RSS feeds)
 */
@Data
public class WebArticle implements Serializable {

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
    private Integer readingTime; // in minutes
    private Integer progress; // reading progress 0-100
    private String category; // inbox, later, archive
    private String language;
    private Integer isRead;
    private Integer isFavorite;
    private Date savedTime;
    private Date createTime;
    private Date updateTime;
    private Integer isDeleted;
}

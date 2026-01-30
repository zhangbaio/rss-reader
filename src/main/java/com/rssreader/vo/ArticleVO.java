package com.rssreader.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ArticleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long feedId;
    private String feedName;
    private String feedImageUrl;
    private String articleGuid;
    private String title;
    private String link;
    private String description;
    private String author;
    private Date pubDate;
    private String category;
    private String enclosureUrl;
    private String enclosureType;
    private String imageUrl;
    private Integer isRead;
    private Integer isFavorite;
    private Date createTime;
}

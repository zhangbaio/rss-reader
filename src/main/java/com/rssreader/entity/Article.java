package com.rssreader.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Article implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long feedId;
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
    private Date updateTime;
    private Integer isDeleted;
}

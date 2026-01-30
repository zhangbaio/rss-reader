package com.rssreader.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ArticleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long feedId;
    private String articleGuid;
    private String title;
    private String link;
    private String description;
    private String author;
    private String pubDate;
    private String category;
    private String enclosureUrl;
    private String enclosureType;
}

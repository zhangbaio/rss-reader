package com.rssreader.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class FeedDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String feedUrl;
    private String feedName;
    private String feedDescription;
    private String feedLink;
    private String feedCategory;
    private String feedLanguage;
    private String feedImageUrl;
    private Integer fetchInterval;
    private Integer status;
}

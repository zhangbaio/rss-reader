package com.rssreader.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Feed implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String feedUrl;
    private String feedName;
    private String feedDescription;
    private String feedLink;
    private String feedCategory;
    private String feedLanguage;
    private String feedImageUrl;
    private Date lastFetchTime;
    private Integer fetchInterval;
    private Integer status;
    private Integer fetchErrorCount;
    private String lastErrorMsg;
    private Date createTime;
    private Date updateTime;
    private Integer isDeleted;
}

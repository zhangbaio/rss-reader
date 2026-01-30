package com.rssreader.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class ArticleContent implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long articleId;
    private String content;
    private String plainText;
    private Integer wordCount;
    private Integer fetchStatus; // 0:未抓取 1:成功 2:失败
    private Date fetchTime;
    private Date createTime;
    private Date updateTime;
}

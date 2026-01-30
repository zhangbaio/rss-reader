package com.rssreader.vo;

import lombok.Data;
import java.util.Date;

@Data
public class ArticleContentVO {
    private Long id;
    private Long articleId;
    private String content;
    private Integer wordCount;
    private Integer fetchStatus;
    private Date fetchTime;
}

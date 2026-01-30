package com.rssreader.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class ArticleReviewFrequency implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long articleId;
    private Integer frequency; // 0: never, 1: less, 2: normally, 3: more
    private Date createTime;
    private Date updateTime;
}

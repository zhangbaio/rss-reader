package com.rssreader.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class ReviewConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Integer dailyCount;
    private Integer recency; // 0-100, 0表示更旧, 100表示更新
    private Boolean includeNew;
    private Boolean includeMastered;
    private Date createTime;
    private Date updateTime;
}

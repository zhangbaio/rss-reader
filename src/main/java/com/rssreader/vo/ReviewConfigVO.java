package com.rssreader.vo;

import lombok.Data;

@Data
public class ReviewConfigVO {
    private Integer dailyCount;
    private Integer recency; // 0-100, 0表示更旧, 100表示更新
    private Boolean includeNew;
    private Boolean includeMastered;
}

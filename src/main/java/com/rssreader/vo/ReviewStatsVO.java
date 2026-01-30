package com.rssreader.vo;

import lombok.Data;

@Data
public class ReviewStatsVO {
    private Integer todayReviewed;
    private Integer totalHighlights;
    private Integer masteredCount;
    private Integer streak;
}

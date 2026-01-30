package com.rssreader.dto;

import lombok.Data;

@Data
public class ReviewConfigDTO {
    private Integer dailyCount;
    private Integer recency;
    private Boolean includeNew;
    private Boolean includeMastered;
}

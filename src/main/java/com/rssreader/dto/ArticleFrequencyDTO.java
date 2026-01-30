package com.rssreader.dto;

import lombok.Data;

@Data
public class ArticleFrequencyDTO {
    private Long articleId;
    private Integer frequency; // 0: never, 1: less, 2: normally, 3: more
}

package com.rssreader.vo;

import lombok.Data;
import java.util.Date;

@Data
public class ArticleFrequencyVO {
    private Long articleId;
    private String title;
    private String author;
    private String feedName;
    private String feedImageUrl;
    private Integer highlightCount;
    private Date latestHighlightTime;
    private Integer frequency; // 0: never, 1: less, 2: normally, 3: more
}

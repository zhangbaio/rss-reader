package com.rssreader.vo;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class HighlightVO {
    private Long id;
    private Long articleId;
    private String selectedText;
    private Integer startOffset;
    private Integer endOffset;
    private String xpath;
    private String color;
    private String note;
    private List<TagVO> tags;
    private Date createTime;
}

package com.rssreader.dto;

import lombok.Data;
import java.util.List;

@Data
public class HighlightDTO {
    private Long articleId;
    private String selectedText;
    private Integer startOffset;
    private Integer endOffset;
    private String xpath;
    private String color;
    private String note;
    private List<String> tagNames; // 标签名称列表
}

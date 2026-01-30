package com.rssreader.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class ArticleHighlight implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long articleId;
    private String selectedText;
    private Integer startOffset;
    private Integer endOffset;
    private String xpath;
    private String color;
    private String note;
    private Date createTime;
    private Date updateTime;

    // 关联的标签
    private List<Tag> tags;
}

package com.rssreader.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class ArticleNote implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long articleId;
    private String noteContent;
    private Date createTime;
    private Date updateTime;
}

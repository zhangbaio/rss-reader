package com.rssreader.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ArticleQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long feedId;
    private Integer isRead;
    private Integer isFavorite;
    private String keyword;
    private Integer page;
    private Integer size;

    public Integer getOffset() {
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }
        return (page - 1) * size;
    }

    public Integer getLimit() {
        if (size == null || size < 1) {
            size = 20;
        }
        return size;
    }
}

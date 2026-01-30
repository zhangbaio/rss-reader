package com.rssreader.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * DTO for creating/updating web article
 */
@Data
public class WebArticleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String url;
    private String title;
    private String category; // inbox, later, archive
}

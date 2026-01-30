package com.rssreader.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class HighlightReview implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long highlightId;
    private String action; // keep, master, feedback, discard
    private Date reviewTime;
    private Date createTime;
}

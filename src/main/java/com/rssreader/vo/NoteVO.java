package com.rssreader.vo;

import lombok.Data;
import java.util.Date;

@Data
public class NoteVO {
    private Long id;
    private Long articleId;
    private String noteContent;
    private Date createTime;
    private Date updateTime;
}

package com.rssreader.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class Tag implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String color;
    private Date createTime;
}

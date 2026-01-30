package com.rssreader.common;

import java.io.Serializable;
import java.util.List;

public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer page;
    private Integer size;
    private Integer total;
    private Integer totalPages;
    private List<T> data;

    public PageResult() {
    }

    public PageResult(Integer page, Integer size, Integer total, List<T> data) {
        this.page = page;
        this.size = size;
        this.total = total;
        this.data = data;
        this.totalPages = (total + size - 1) / size;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}

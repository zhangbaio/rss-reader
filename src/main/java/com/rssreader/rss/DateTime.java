package com.rssreader.rss;

import java.util.Date;

public class DateTime {
    private Date date;
    private String original;

    public DateTime(Date date, String original) {
        this.date = date;
        this.original = original;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }
}

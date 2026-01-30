package com.rssreader.exception;

public class RssReaderException extends RuntimeException {

    private Integer code;

    public RssReaderException(String message) {
        super(message);
        this.code = 500;
    }

    public RssReaderException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public RssReaderException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}

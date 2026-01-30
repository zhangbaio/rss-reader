package com.rssreader.exception;

import com.rssreader.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RssReaderException.class)
    public Result<?> handleRssReaderException(RssReaderException e) {
        logger.error("RssReaderException: {}", e.getMessage(), e);
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        logger.error("Unexpected exception: {}", e.getMessage(), e);
        return Result.error("Internal server error: " + e.getMessage());
    }
}

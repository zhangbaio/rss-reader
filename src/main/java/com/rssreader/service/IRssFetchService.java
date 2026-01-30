package com.rssreader.service;

public interface IRssFetchService {

    void fetchFeed(Long feedId);

    void fetchAllFeeds();
}

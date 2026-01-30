package com.rssreader.service.impl;

import com.rssreader.entity.Article;
import com.rssreader.entity.Feed;
import com.rssreader.exception.RssReaderException;
import com.rssreader.mapper.ArticleMapper;
import com.rssreader.mapper.FeedMapper;
import com.rssreader.rss.Channel;
import com.rssreader.service.IRssFetchService;
import com.rssreader.util.HttpUtils;
import com.rssreader.util.RssParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class RssFetchServiceImpl implements IRssFetchService {

    private static final Logger logger = LoggerFactory.getLogger(RssFetchServiceImpl.class);

    @Autowired
    private FeedMapper feedMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Value("${rss.fetch.thread-pool-size:5}")
    private Integer threadPoolSize;

    @Value("${rss.fetch.max-retries:3}")
    private Integer maxRetries;

    @Value("${rss.fetch.timeout:30000}")
    private Integer timeout;

    @Override
    @Transactional
    public void fetchFeed(Long feedId) {
        Feed feed = feedMapper.selectById(feedId);
        if (feed == null) {
            throw new RssReaderException("Feed not found");
        }

        int retries = 0;
        Exception lastException = null;

        while (retries < maxRetries) {
            try {
                logger.info("Fetching feed: {} (attempt {}/{})", feed.getFeedUrl(), retries + 1, maxRetries);

                String xml = HttpUtils.get(feed.getFeedUrl(), timeout);
                Channel channel = RssParser.parseFeed(xml);

                if (feed.getFeedName() == null || feed.getFeedName().isEmpty()) {
                    feed.setFeedName(channel.getTitle());
                }
                if (feed.getFeedDescription() == null || feed.getFeedDescription().isEmpty()) {
                    feed.setFeedDescription(channel.getDescription());
                }
                if (feed.getFeedLink() == null || feed.getFeedLink().isEmpty()) {
                    feed.setFeedLink(channel.getLink());
                }
                if (feed.getFeedLanguage() == null || feed.getFeedLanguage().isEmpty()) {
                    feed.setFeedLanguage(channel.getLanguage());
                }
                if (channel.getImage() != null && (feed.getFeedImageUrl() == null || feed.getFeedImageUrl().isEmpty())) {
                    feed.setFeedImageUrl(channel.getImage().getUrl());
                }
                feedMapper.updateById(feed);

                List<Article> articles = RssParser.convertToArticles(channel, feedId);
                saveArticles(articles);

                feedMapper.updateLastFetchTime(feedId);
                feedMapper.resetFetchError(feedId);

                logger.info("Successfully fetched {} articles from feed: {}", articles.size(), feed.getFeedUrl());
                return;

            } catch (Exception e) {
                lastException = e;
                retries++;
                logger.error("Failed to fetch feed: {} (attempt {}/{})", feed.getFeedUrl(), retries, maxRetries, e);

                if (retries < maxRetries) {
                    try {
                        Thread.sleep(2000 * retries);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        String errorMsg = lastException != null ? lastException.getMessage() : "Unknown error";
        feedMapper.updateFetchError(feedId, errorMsg);
        throw new RssReaderException("Failed to fetch feed after " + maxRetries + " retries: " + errorMsg);
    }

    @Override
    public void fetchAllFeeds() {
        List<Feed> feeds = feedMapper.selectAllEnabled();
        if (feeds == null || feeds.isEmpty()) {
            logger.info("No enabled feeds to fetch");
            return;
        }

        logger.info("Starting to fetch {} feeds", feeds.size());

        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);

        for (Feed feed : feeds) {
            executor.submit(() -> {
                try {
                    fetchFeed(feed.getId());
                } catch (Exception e) {
                    logger.error("Error fetching feed {}: {}", feed.getFeedUrl(), e.getMessage());
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(30, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            logger.error("Feed fetch interrupted", e);
            Thread.currentThread().interrupt();
        }

        logger.info("Completed fetching all feeds");
    }

    private void saveArticles(List<Article> articles) {
        if (articles == null || articles.isEmpty()) {
            return;
        }

        Long feedId = articles.get(0).getFeedId();
        List<String> guids = articles.stream()
                .map(Article::getArticleGuid)
                .collect(Collectors.toList());

        List<String> existingGuids = articleMapper.selectExistingGuids(feedId, guids);

        List<Article> newArticles = articles.stream()
                .filter(article -> !existingGuids.contains(article.getArticleGuid()))
                .collect(Collectors.toList());

        if (!newArticles.isEmpty()) {
            logger.info("Saving {} new articles for feed {}", newArticles.size(), feedId);
            articleMapper.batchInsert(newArticles);
        } else {
            logger.info("No new articles found for feed {}", feedId);
        }
    }
}

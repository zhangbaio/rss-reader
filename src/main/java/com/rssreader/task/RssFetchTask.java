package com.rssreader.task;

import com.rssreader.service.IRssFetchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "rss.fetch", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RssFetchTask {

    private static final Logger logger = LoggerFactory.getLogger(RssFetchTask.class);

    @Autowired
    private IRssFetchService rssFetchService;

    @Value("${rss.fetch.enabled:true}")
    private Boolean enabled;

    @Scheduled(cron = "${rss.fetch.cron:0 */30 * * * ?}")
    public void scheduledFetch() {
        if (!enabled) {
            logger.info("RSS fetch is disabled, skipping scheduled task");
            return;
        }

        logger.info("Starting scheduled RSS fetch task");
        try {
            rssFetchService.fetchAllFeeds();
            logger.info("Scheduled RSS fetch task completed successfully");
        } catch (Exception e) {
            logger.error("Error in scheduled RSS fetch task", e);
        }
    }
}

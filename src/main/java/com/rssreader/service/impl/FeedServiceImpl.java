package com.rssreader.service.impl;

import com.rssreader.common.Constants;
import com.rssreader.common.PageResult;
import com.rssreader.dto.FeedDTO;
import com.rssreader.entity.Feed;
import com.rssreader.exception.RssReaderException;
import com.rssreader.mapper.FeedMapper;
import com.rssreader.service.IFeedService;
import com.rssreader.service.IRssFetchService;
import com.rssreader.vo.FeedVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FeedServiceImpl implements IFeedService {

    @Autowired
    private FeedMapper feedMapper;

    @Autowired
    private IRssFetchService rssFetchService;

    @Override
    @Transactional
    public Long addFeed(FeedDTO feedDTO) {
        Feed existingFeed = feedMapper.selectByUrl(feedDTO.getFeedUrl());
        if (existingFeed != null) {
            throw new RssReaderException("Feed URL already exists");
        }

        Feed feed = new Feed();
        BeanUtils.copyProperties(feedDTO, feed);

        if (feed.getFetchInterval() == null) {
            feed.setFetchInterval(Constants.DEFAULT_FETCH_INTERVAL);
        }
        if (feed.getStatus() == null) {
            feed.setStatus(Constants.STATUS_ENABLED);
        }
        feed.setFetchErrorCount(0);

        feedMapper.insert(feed);

        try {
            rssFetchService.fetchFeed(feed.getId());
        } catch (Exception e) {
            throw new RssReaderException("Failed to fetch feed: " + e.getMessage(), e);
        }

        return feed.getId();
    }

    @Override
    @Transactional
    public void deleteFeed(Long id) {
        Feed feed = feedMapper.selectById(id);
        if (feed == null) {
            throw new RssReaderException("Feed not found");
        }
        feedMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void updateFeed(Long id, FeedDTO feedDTO) {
        Feed feed = feedMapper.selectById(id);
        if (feed == null) {
            throw new RssReaderException("Feed not found");
        }

        BeanUtils.copyProperties(feedDTO, feed);
        feed.setId(id);
        feedMapper.updateById(feed);
    }

    @Override
    public PageResult<FeedVO> listFeeds(Integer page, Integer size) {
        if (page == null || page < 1) {
            page = Constants.DEFAULT_PAGE;
        }
        if (size == null || size < 1) {
            size = Constants.DEFAULT_SIZE;
        }

        Integer offset = (page - 1) * size;
        List<FeedVO> feeds = feedMapper.selectList(offset, size);
        Integer total = feedMapper.selectCount();

        return new PageResult<>(page, size, total, feeds);
    }

    @Override
    public FeedVO getFeedDetail(Long id) {
        Feed feed = feedMapper.selectById(id);
        if (feed == null) {
            throw new RssReaderException("Feed not found");
        }

        FeedVO feedVO = new FeedVO();
        BeanUtils.copyProperties(feed, feedVO);
        return feedVO;
    }

    @Override
    public void fetchFeed(Long id) {
        Feed feed = feedMapper.selectById(id);
        if (feed == null) {
            throw new RssReaderException("Feed not found");
        }

        rssFetchService.fetchFeed(id);
    }
}

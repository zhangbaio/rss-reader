package com.rssreader.service;

import com.rssreader.common.PageResult;
import com.rssreader.dto.FeedDTO;
import com.rssreader.entity.Feed;
import com.rssreader.vo.FeedVO;

public interface IFeedService {

    Long addFeed(FeedDTO feedDTO);

    void deleteFeed(Long id);

    void updateFeed(Long id, FeedDTO feedDTO);

    PageResult<FeedVO> listFeeds(Integer page, Integer size);

    FeedVO getFeedDetail(Long id);

    void fetchFeed(Long id);
}

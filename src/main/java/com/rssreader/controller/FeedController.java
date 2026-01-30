package com.rssreader.controller;

import com.rssreader.common.PageResult;
import com.rssreader.common.Result;
import com.rssreader.dto.FeedDTO;
import com.rssreader.service.IFeedService;
import com.rssreader.service.IRssFetchService;
import com.rssreader.vo.FeedVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feeds")
public class FeedController {

    @Autowired
    private IFeedService feedService;

    @Autowired
    private IRssFetchService rssFetchService;

    @PostMapping
    public Result<Long> addFeed(@RequestBody FeedDTO feedDTO) {
        Long feedId = feedService.addFeed(feedDTO);
        return Result.success("Feed added successfully", feedId);
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteFeed(@PathVariable Long id) {
        feedService.deleteFeed(id);
        return Result.success("Feed deleted successfully");
    }

    @PutMapping("/{id}")
    public Result<String> updateFeed(@PathVariable Long id, @RequestBody FeedDTO feedDTO) {
        feedService.updateFeed(id, feedDTO);
        return Result.success("Feed updated successfully");
    }

    @GetMapping
    public Result<PageResult<FeedVO>> listFeeds(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        PageResult<FeedVO> result = feedService.listFeeds(page, size);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<FeedVO> getFeedDetail(@PathVariable Long id) {
        FeedVO feed = feedService.getFeedDetail(id);
        return Result.success(feed);
    }

    @PostMapping("/{id}/fetch")
    public Result<String> fetchFeed(@PathVariable Long id) {
        feedService.fetchFeed(id);
        return Result.success("Feed fetched successfully");
    }

    @PostMapping("/fetch-all")
    public Result<String> fetchAllFeeds() {
        rssFetchService.fetchAllFeeds();
        return Result.success("All feeds fetch started");
    }
}

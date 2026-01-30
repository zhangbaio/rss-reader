package com.rssreader.mapper;

import com.rssreader.entity.Feed;
import com.rssreader.vo.FeedVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FeedMapper {

    int insert(Feed feed);

    int updateById(Feed feed);

    int deleteById(@Param("id") Long id);

    Feed selectById(@Param("id") Long id);

    Feed selectByUrl(@Param("feedUrl") String feedUrl);

    List<FeedVO> selectList(@Param("offset") Integer offset, @Param("limit") Integer limit);

    int selectCount();

    List<Feed> selectAllEnabled();

    int updateLastFetchTime(@Param("id") Long id);

    int updateFetchError(@Param("id") Long id, @Param("errorMsg") String errorMsg);

    int resetFetchError(@Param("id") Long id);
}

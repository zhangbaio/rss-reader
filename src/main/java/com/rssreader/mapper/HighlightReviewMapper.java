package com.rssreader.mapper;

import com.rssreader.entity.HighlightReview;
import com.rssreader.vo.ReviewHighlightVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface HighlightReviewMapper {

    /**
     * 获取今日需要复习的高亮列表
     */
    List<ReviewHighlightVO> findDailyReviewList(@Param("limit") Integer limit,
                                                 @Param("includeNew") Boolean includeNew,
                                                 @Param("includeMastered") Boolean includeMastered);

    /**
     * 根据高亮ID获取复习次数
     */
    Integer getReviewCountByHighlightId(@Param("highlightId") Long highlightId);

    /**
     * 获取最后一次复习时间
     */
    Date getLastReviewTimeByHighlightId(@Param("highlightId") Long highlightId);

    /**
     * 获取掌握程度
     */
    Integer getMasteryLevelByHighlightId(@Param("highlightId") Long highlightId);

    /**
     * 插入复习记录
     */
    int insert(HighlightReview review);

    /**
     * 获取今日复习数量
     */
    Integer getTodayReviewedCount();

    /**
     * 获取已掌握的高亮数量
     */
    Integer getMasteredCount();

    /**
     * 获取复习连续天数
     */
    Integer getReviewStreak();

    /**
     * 获取所有高亮数量
     */
    Integer getTotalHighlightsCount();
}

package com.rssreader.service.impl;

import com.rssreader.dto.ArticleFrequencyDTO;
import com.rssreader.dto.ReviewConfigDTO;
import com.rssreader.entity.ArticleReviewFrequency;
import com.rssreader.entity.HighlightReview;
import com.rssreader.entity.ReviewConfig;
import com.rssreader.entity.Tag;
import com.rssreader.mapper.ArticleReviewFrequencyMapper;
import com.rssreader.mapper.HighlightReviewMapper;
import com.rssreader.mapper.ReviewConfigMapper;
import com.rssreader.mapper.TagMapper;
import com.rssreader.service.IReviewService;
import com.rssreader.vo.ArticleFrequencyVO;
import com.rssreader.vo.ReviewConfigVO;
import com.rssreader.vo.ReviewHighlightVO;
import com.rssreader.vo.ReviewStatsVO;
import com.rssreader.vo.TagVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements IReviewService {

    @Autowired
    private HighlightReviewMapper highlightReviewMapper;

    @Autowired
    private ReviewConfigMapper reviewConfigMapper;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private ArticleReviewFrequencyMapper articleFrequencyMapper;

    @Override
    public List<ReviewHighlightVO> getDailyReviewList() {
        ReviewConfig config = getOrCreateConfig();

        List<ReviewHighlightVO> highlights = highlightReviewMapper.findDailyReviewList(
                config.getDailyCount(),
                config.getIncludeNew(),
                config.getIncludeMastered()
        );

        // 填充标签信息
        for (ReviewHighlightVO highlight : highlights) {
            List<Tag> tags = tagMapper.findByHighlightId(highlight.getId());
            highlight.setTags(tags.stream().map(this::toTagVO).collect(Collectors.toList()));
        }

        return highlights;
    }

    @Override
    @Transactional
    public void recordReviewAction(Long highlightId, String action) {
        // 验证action
        if (!isValidAction(action)) {
            throw new IllegalArgumentException("Invalid action: " + action);
        }

        HighlightReview review = new HighlightReview();
        review.setHighlightId(highlightId);
        review.setAction(action);
        review.setReviewTime(new Date());

        highlightReviewMapper.insert(review);
    }

    @Override
    public ReviewStatsVO getReviewStats() {
        ReviewStatsVO stats = new ReviewStatsVO();

        Integer todayReviewed = highlightReviewMapper.getTodayReviewedCount();
        Integer totalHighlights = highlightReviewMapper.getTotalHighlightsCount();
        Integer masteredCount = highlightReviewMapper.getMasteredCount();
        Integer streak = highlightReviewMapper.getReviewStreak();

        stats.setTodayReviewed(todayReviewed != null ? todayReviewed : 0);
        stats.setTotalHighlights(totalHighlights != null ? totalHighlights : 0);
        stats.setMasteredCount(masteredCount != null ? masteredCount : 0);
        stats.setStreak(streak != null ? streak : 0);

        return stats;
    }

    @Override
    public ReviewConfigVO getReviewConfig() {
        ReviewConfig config = getOrCreateConfig();
        return toConfigVO(config);
    }

    @Override
    @Transactional
    public ReviewConfigVO updateReviewConfig(ReviewConfigDTO dto) {
        ReviewConfig config = getOrCreateConfig();

        if (dto.getDailyCount() != null) {
            config.setDailyCount(dto.getDailyCount());
        }
        if (dto.getRecency() != null) {
            config.setRecency(dto.getRecency());
        }
        if (dto.getIncludeNew() != null) {
            config.setIncludeNew(dto.getIncludeNew());
        }
        if (dto.getIncludeMastered() != null) {
            config.setIncludeMastered(dto.getIncludeMastered());
        }

        reviewConfigMapper.update(config);
        return toConfigVO(config);
    }

    @Override
    public List<ArticleFrequencyVO> getArticlesWithFrequency(String keyword) {
        return articleFrequencyMapper.findArticlesWithHighlights(keyword);
    }

    @Override
    @Transactional
    public void updateArticleFrequency(ArticleFrequencyDTO dto) {
        ArticleReviewFrequency existing = articleFrequencyMapper.findByArticleId(dto.getArticleId());
        if (existing == null) {
            ArticleReviewFrequency frequency = new ArticleReviewFrequency();
            frequency.setArticleId(dto.getArticleId());
            frequency.setFrequency(dto.getFrequency());
            articleFrequencyMapper.insert(frequency);
        } else {
            existing.setFrequency(dto.getFrequency());
            articleFrequencyMapper.update(existing);
        }
    }

    @Override
    @Transactional
    public void batchUpdateArticleFrequency(List<Long> articleIds, Integer frequency) {
        if (articleIds == null || articleIds.isEmpty()) {
            return;
        }
        for (Long articleId : articleIds) {
            ArticleFrequencyDTO dto = new ArticleFrequencyDTO();
            dto.setArticleId(articleId);
            dto.setFrequency(frequency);
            updateArticleFrequency(dto);
        }
    }

    private ReviewConfig getOrCreateConfig() {
        ReviewConfig config = reviewConfigMapper.findConfig();
        if (config == null) {
            config = new ReviewConfig();
            config.setDailyCount(10);
            config.setRecency(50);
            config.setIncludeNew(true);
            config.setIncludeMastered(false);
            reviewConfigMapper.insert(config);
        }
        return config;
    }

    private boolean isValidAction(String action) {
        return "keep".equals(action) ||
               "master".equals(action) ||
               "feedback".equals(action) ||
               "discard".equals(action);
    }

    private ReviewConfigVO toConfigVO(ReviewConfig config) {
        ReviewConfigVO vo = new ReviewConfigVO();
        vo.setDailyCount(config.getDailyCount());
        vo.setRecency(config.getRecency() != null ? config.getRecency() : 50);
        vo.setIncludeNew(config.getIncludeNew());
        vo.setIncludeMastered(config.getIncludeMastered());
        return vo;
    }

    private TagVO toTagVO(Tag tag) {
        if (tag == null) return null;
        TagVO vo = new TagVO();
        BeanUtils.copyProperties(tag, vo);
        return vo;
    }
}

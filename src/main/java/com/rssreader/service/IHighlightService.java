package com.rssreader.service;

import com.rssreader.dto.HighlightDTO;
import com.rssreader.vo.HighlightVO;
import com.rssreader.vo.TagVO;
import java.util.List;

public interface IHighlightService {

    /**
     * 获取文章的所有划线
     */
    List<HighlightVO> getHighlightsByArticleId(Long articleId);

    /**
     * 创建划线
     */
    HighlightVO createHighlight(HighlightDTO dto);

    /**
     * 更新划线
     */
    HighlightVO updateHighlight(Long id, HighlightDTO dto);

    /**
     * 删除划线
     */
    void deleteHighlight(Long id);

    /**
     * 获取所有标签
     */
    List<TagVO> getAllTags();

    /**
     * 创建标签
     */
    TagVO createTag(String name, String color);

    /**
     * 删除标签
     */
    void deleteTag(Long id);
}

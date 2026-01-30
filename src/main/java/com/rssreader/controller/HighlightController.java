package com.rssreader.controller;

import com.rssreader.common.Result;
import com.rssreader.dto.HighlightDTO;
import com.rssreader.service.IHighlightService;
import com.rssreader.vo.HighlightVO;
import com.rssreader.vo.TagVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/highlights")
public class HighlightController {

    @Autowired
    private IHighlightService highlightService;

    /**
     * 获取文章的所有划线
     */
    @GetMapping("/article/{articleId}")
    public Result<List<HighlightVO>> getHighlightsByArticle(@PathVariable Long articleId) {
        List<HighlightVO> highlights = highlightService.getHighlightsByArticleId(articleId);
        return Result.success(highlights);
    }

    /**
     * 创建划线
     */
    @PostMapping
    public Result<HighlightVO> createHighlight(@RequestBody HighlightDTO dto) {
        HighlightVO highlight = highlightService.createHighlight(dto);
        return Result.success(highlight);
    }

    /**
     * 更新划线
     */
    @PutMapping("/{id}")
    public Result<HighlightVO> updateHighlight(
            @PathVariable Long id,
            @RequestBody HighlightDTO dto) {
        HighlightVO highlight = highlightService.updateHighlight(id, dto);
        return Result.success(highlight);
    }

    /**
     * 删除划线
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteHighlight(@PathVariable Long id) {
        highlightService.deleteHighlight(id);
        return Result.success("Highlight deleted");
    }

    /**
     * 获取所有标签
     */
    @GetMapping("/tags")
    public Result<List<TagVO>> getAllTags() {
        List<TagVO> tags = highlightService.getAllTags();
        return Result.success(tags);
    }

    /**
     * 创建标签
     */
    @PostMapping("/tags")
    public Result<TagVO> createTag(
            @RequestParam String name,
            @RequestParam(required = false) String color) {
        TagVO tag = highlightService.createTag(name, color);
        return Result.success(tag);
    }

    /**
     * 删除标签
     */
    @DeleteMapping("/tags/{id}")
    public Result<String> deleteTag(@PathVariable Long id) {
        highlightService.deleteTag(id);
        return Result.success("Tag deleted");
    }
}

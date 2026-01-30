package com.rssreader.service.impl;

import com.rssreader.dto.HighlightDTO;
import com.rssreader.entity.ArticleHighlight;
import com.rssreader.entity.Tag;
import com.rssreader.mapper.ArticleHighlightMapper;
import com.rssreader.mapper.TagMapper;
import com.rssreader.service.IHighlightService;
import com.rssreader.vo.HighlightVO;
import com.rssreader.vo.TagVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HighlightServiceImpl implements IHighlightService {

    @Autowired
    private ArticleHighlightMapper highlightMapper;

    @Autowired
    private TagMapper tagMapper;

    @Override
    public List<HighlightVO> getHighlightsByArticleId(Long articleId) {
        List<ArticleHighlight> highlights = highlightMapper.findByArticleId(articleId);
        return highlights.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HighlightVO createHighlight(HighlightDTO dto) {
        ArticleHighlight highlight = new ArticleHighlight();
        BeanUtils.copyProperties(dto, highlight);

        highlightMapper.insert(highlight);

        // 处理标签
        if (dto.getTagNames() != null && !dto.getTagNames().isEmpty()) {
            for (String tagName : dto.getTagNames()) {
                Tag tag = tagMapper.findByName(tagName);
                if (tag == null) {
                    tag = new Tag();
                    tag.setName(tagName);
                    tag.setColor("#409eff");
                    tagMapper.insert(tag);
                }
                tagMapper.insertHighlightTag(highlight.getId(), tag.getId());
            }
        }

        return toVO(highlightMapper.findById(highlight.getId()));
    }

    @Override
    @Transactional
    public HighlightVO updateHighlight(Long id, HighlightDTO dto) {
        ArticleHighlight highlight = highlightMapper.findById(id);
        if (highlight == null) {
            throw new RuntimeException("Highlight not found");
        }

        highlight.setSelectedText(dto.getSelectedText());
        highlight.setColor(dto.getColor());
        highlight.setNote(dto.getNote());
        if (dto.getStartOffset() != null) {
            highlight.setStartOffset(dto.getStartOffset());
        }
        if (dto.getEndOffset() != null) {
            highlight.setEndOffset(dto.getEndOffset());
        }
        if (dto.getXpath() != null) {
            highlight.setXpath(dto.getXpath());
        }

        highlightMapper.update(highlight);

        // 更新标签
        tagMapper.deleteHighlightTags(id);
        if (dto.getTagNames() != null && !dto.getTagNames().isEmpty()) {
            for (String tagName : dto.getTagNames()) {
                Tag tag = tagMapper.findByName(tagName);
                if (tag == null) {
                    tag = new Tag();
                    tag.setName(tagName);
                    tag.setColor("#409eff");
                    tagMapper.insert(tag);
                }
                tagMapper.insertHighlightTag(id, tag.getId());
            }
        }

        return toVO(highlightMapper.findById(id));
    }

    @Override
    @Transactional
    public void deleteHighlight(Long id) {
        tagMapper.deleteHighlightTags(id);
        highlightMapper.deleteById(id);
    }

    @Override
    public List<TagVO> getAllTags() {
        List<Tag> tags = tagMapper.findAll();
        return tags.stream().map(this::toTagVO).collect(Collectors.toList());
    }

    @Override
    public TagVO createTag(String name, String color) {
        Tag existing = tagMapper.findByName(name);
        if (existing != null) {
            return toTagVO(existing);
        }

        Tag tag = new Tag();
        tag.setName(name);
        tag.setColor(color != null ? color : "#409eff");
        tagMapper.insert(tag);

        return toTagVO(tag);
    }

    @Override
    public void deleteTag(Long id) {
        tagMapper.deleteById(id);
    }

    private HighlightVO toVO(ArticleHighlight highlight) {
        if (highlight == null) return null;

        HighlightVO vo = new HighlightVO();
        BeanUtils.copyProperties(highlight, vo);

        // 获取标签
        List<Tag> tags = tagMapper.findByHighlightId(highlight.getId());
        vo.setTags(tags.stream().map(this::toTagVO).collect(Collectors.toList()));

        return vo;
    }

    private TagVO toTagVO(Tag tag) {
        if (tag == null) return null;

        TagVO vo = new TagVO();
        BeanUtils.copyProperties(tag, vo);
        return vo;
    }
}

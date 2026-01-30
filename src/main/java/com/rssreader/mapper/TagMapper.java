package com.rssreader.mapper;

import com.rssreader.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface TagMapper {

    List<Tag> findAll();

    Tag findById(@Param("id") Long id);

    Tag findByName(@Param("name") String name);

    List<Tag> findByHighlightId(@Param("highlightId") Long highlightId);

    int insert(Tag tag);

    int update(Tag tag);

    int deleteById(@Param("id") Long id);

    // 划线标签关联
    int insertHighlightTag(@Param("highlightId") Long highlightId, @Param("tagId") Long tagId);

    int deleteHighlightTags(@Param("highlightId") Long highlightId);
}

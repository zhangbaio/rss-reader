package com.rssreader.service;

import com.rssreader.dto.NoteDTO;
import com.rssreader.vo.NoteVO;

public interface INoteService {

    /**
     * 获取文章笔记
     */
    NoteVO getNoteByArticleId(Long articleId);

    /**
     * 保存文章笔记（创建或更新）
     */
    NoteVO saveNote(NoteDTO dto);

    /**
     * 删除文章笔记
     */
    void deleteNote(Long articleId);
}

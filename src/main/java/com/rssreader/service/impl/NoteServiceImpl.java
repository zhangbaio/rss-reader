package com.rssreader.service.impl;

import com.rssreader.dto.NoteDTO;
import com.rssreader.entity.ArticleNote;
import com.rssreader.mapper.ArticleNoteMapper;
import com.rssreader.service.INoteService;
import com.rssreader.vo.NoteVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NoteServiceImpl implements INoteService {

    @Autowired
    private ArticleNoteMapper noteMapper;

    @Override
    public NoteVO getNoteByArticleId(Long articleId) {
        ArticleNote note = noteMapper.findByArticleId(articleId);
        return toVO(note);
    }

    @Override
    public NoteVO saveNote(NoteDTO dto) {
        ArticleNote existing = noteMapper.findByArticleId(dto.getArticleId());

        if (existing != null) {
            existing.setNoteContent(dto.getNoteContent());
            noteMapper.update(existing);
            return toVO(noteMapper.findByArticleId(dto.getArticleId()));
        } else {
            ArticleNote note = new ArticleNote();
            note.setArticleId(dto.getArticleId());
            note.setNoteContent(dto.getNoteContent());
            noteMapper.insert(note);
            return toVO(note);
        }
    }

    @Override
    public void deleteNote(Long articleId) {
        noteMapper.deleteByArticleId(articleId);
    }

    private NoteVO toVO(ArticleNote note) {
        if (note == null) return null;

        NoteVO vo = new NoteVO();
        BeanUtils.copyProperties(note, vo);
        return vo;
    }
}

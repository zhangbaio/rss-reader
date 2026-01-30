package com.rssreader.controller;

import com.rssreader.common.Result;
import com.rssreader.dto.NoteDTO;
import com.rssreader.service.INoteService;
import com.rssreader.vo.NoteVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    @Autowired
    private INoteService noteService;

    /**
     * 获取文章笔记
     */
    @GetMapping("/article/{articleId}")
    public Result<NoteVO> getNoteByArticle(@PathVariable Long articleId) {
        NoteVO note = noteService.getNoteByArticleId(articleId);
        return Result.success(note);
    }

    /**
     * 保存文章笔记
     */
    @PostMapping
    public Result<NoteVO> saveNote(@RequestBody NoteDTO dto) {
        NoteVO note = noteService.saveNote(dto);
        return Result.success(note);
    }

    /**
     * 删除文章笔记
     */
    @DeleteMapping("/article/{articleId}")
    public Result<String> deleteNote(@PathVariable Long articleId) {
        noteService.deleteNote(articleId);
        return Result.success("Note deleted");
    }
}

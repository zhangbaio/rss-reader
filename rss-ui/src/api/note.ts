import request from './index'
import type { ArticleNote } from '@/types'

// 获取文章笔记
export function getNoteByArticle(articleId: number): Promise<ArticleNote | null> {
  return request.get(`/notes/article/${articleId}`)
}

// 保存文章笔记
export function saveNote(articleId: number, noteContent: string): Promise<ArticleNote> {
  return request.post('/notes', { articleId, noteContent })
}

// 删除文章笔记
export function deleteNote(articleId: number): Promise<void> {
  return request.delete(`/notes/article/${articleId}`)
}

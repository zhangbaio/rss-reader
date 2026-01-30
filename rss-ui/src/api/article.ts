import request from './index'
import type { Article, ArticleQueryParams, PageResult } from '@/types'

// 获取文章列表
export function getArticles(params: ArticleQueryParams): Promise<PageResult<Article>> {
  return request.get('/articles', { params })
}

// 获取文章详情
export function getArticle(id: number): Promise<Article> {
  return request.get(`/articles/${id}`)
}

// 标记文章为已读
export function markAsRead(id: number): Promise<void> {
  return request.put(`/articles/${id}/read`, null, { params: { isRead: 1 } })
}

// 标记文章为未读
export function markAsUnread(id: number): Promise<void> {
  return request.put(`/articles/${id}/read`, null, { params: { isRead: 0 } })
}

// 批量标记为已读
export function batchMarkAsRead(ids: number[]): Promise<void> {
  return request.post('/articles/batch-read', ids, { params: { isRead: 1 } })
}

// 切换收藏状态
export function toggleFavorite(id: number): Promise<void> {
  return request.put(`/articles/${id}/favorite`)
}

// 获取未读数量
export function getUnreadCount(feedId?: number): Promise<number> {
  return request.get('/articles/unread-count', { params: { feedId } })
}

// 获取文章原文内容
export function getFullContent(id: number): Promise<string> {
  return request.get(`/articles/${id}/fullcontent`)
}

// 清除原文缓存
export function clearFullContentCache(id: number): Promise<void> {
  return request.delete(`/articles/${id}/fullcontent/cache`)
}

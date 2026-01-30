import request from './index'
import type { WebArticle, WebArticleQueryParams, AddWebArticleParams, PageResult, CategoryCounts } from '@/types'

// Add article from URL
export function addWebArticle(params: AddWebArticleParams): Promise<WebArticle> {
  return request.post('/web-articles', params)
}

// Get article by ID
export function getWebArticle(id: number): Promise<WebArticle> {
  return request.get(`/web-articles/${id}`)
}

// Get article list
export function getWebArticles(params: WebArticleQueryParams): Promise<PageResult<WebArticle>> {
  return request.get('/web-articles', { params })
}

// Update article category
export function updateCategory(id: number, category: string): Promise<void> {
  return request.put(`/web-articles/${id}/category`, null, { params: { category } })
}

// Mark article as read
export function markAsRead(id: number): Promise<void> {
  return request.put(`/web-articles/${id}/read`, null, { params: { isRead: 1 } })
}

// Mark article as unread
export function markAsUnread(id: number): Promise<void> {
  return request.put(`/web-articles/${id}/read`, null, { params: { isRead: 0 } })
}

// Toggle favorite status
export function toggleFavorite(id: number): Promise<void> {
  return request.put(`/web-articles/${id}/favorite`)
}

// Update reading progress
export function updateProgress(id: number, progress: number): Promise<void> {
  return request.put(`/web-articles/${id}/progress`, null, { params: { progress } })
}

// Delete article
export function deleteWebArticle(id: number): Promise<void> {
  return request.delete(`/web-articles/${id}`)
}

// Get category counts
export function getCategoryCounts(): Promise<CategoryCounts> {
  return request.get('/web-articles/counts')
}

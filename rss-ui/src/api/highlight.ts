import request from './index'
import type { Highlight, CreateHighlightParams, UpdateHighlightParams, Tag } from '@/types'

// 获取文章的所有划线
export function getHighlightsByArticle(articleId: number): Promise<Highlight[]> {
  return request.get(`/highlights/article/${articleId}`)
}

// 创建划线
export function createHighlight(params: CreateHighlightParams): Promise<Highlight> {
  return request.post('/highlights', params)
}

// 更新划线
export function updateHighlight(id: number, params: UpdateHighlightParams): Promise<Highlight> {
  return request.put(`/highlights/${id}`, params)
}

// 删除划线
export function deleteHighlight(id: number): Promise<void> {
  return request.delete(`/highlights/${id}`)
}

// 获取所有标签
export function getAllTags(): Promise<Tag[]> {
  return request.get('/highlights/tags')
}

// 创建标签
export function createTag(name: string, color?: string): Promise<Tag> {
  return request.post('/highlights/tags', null, { params: { name, color } })
}

// 删除标签
export function deleteTag(id: number): Promise<void> {
  return request.delete(`/highlights/tags/${id}`)
}

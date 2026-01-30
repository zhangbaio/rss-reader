import request from './index'
import type { Feed, PageResult, AddFeedParams, UpdateFeedParams } from '@/types'

// 获取所有订阅源
export function getFeeds(page = 1, size = 100): Promise<PageResult<Feed>> {
  return request.get('/feeds', { params: { page, size } })
}

// 获取单个订阅源
export function getFeed(id: number): Promise<Feed> {
  return request.get(`/feeds/${id}`)
}

// 添加订阅源
export function addFeed(params: AddFeedParams): Promise<number> {
  return request.post('/feeds', params)
}

// 更新订阅源
export function updateFeed(id: number, params: UpdateFeedParams): Promise<void> {
  return request.put(`/feeds/${id}`, params)
}

// 删除订阅源
export function deleteFeed(id: number): Promise<void> {
  return request.delete(`/feeds/${id}`)
}

// 刷新订阅源
export function refreshFeed(id: number): Promise<void> {
  return request.post(`/feeds/${id}/fetch`)
}

// 刷新所有订阅源
export function refreshAllFeeds(): Promise<void> {
  return request.post('/feeds/fetch-all')
}

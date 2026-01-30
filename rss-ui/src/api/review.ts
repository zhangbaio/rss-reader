import request from './index'
import type { ReviewHighlight, ReviewAction, ReviewConfig, ArticleFrequency } from '@/types'

// 获取今日复习列表
export function getDailyReviewList(): Promise<ReviewHighlight[]> {
  return request.get('/review/daily')
}

// 记录复习操作
export function recordReviewAction(highlightId: number, action: ReviewAction): Promise<void> {
  return request.post(`/review/${highlightId}/action`, { action })
}

// 获取复习统计
export function getReviewStats(): Promise<{
  todayReviewed: number
  totalHighlights: number
  masteredCount: number
  streak: number
}> {
  return request.get('/review/stats')
}

// 获取复习配置
export function getReviewConfig(): Promise<ReviewConfig> {
  return request.get('/review/config')
}

// 更新复习配置
export function updateReviewConfig(config: Partial<ReviewConfig>): Promise<ReviewConfig> {
  return request.put('/review/config', config)
}

// 获取有高亮的文章列表及其频率设置
export function getArticlesWithFrequency(keyword?: string): Promise<ArticleFrequency[]> {
  return request.get('/review/articles/frequency', { params: { keyword } })
}

// 更新单篇文章的复习频率
export function updateArticleFrequency(articleId: number, frequency: number): Promise<void> {
  return request.put(`/review/articles/${articleId}/frequency`, { frequency })
}

// 批量更新文章复习频率
export function batchUpdateArticleFrequency(articleIds: number[], frequency: number): Promise<void> {
  return request.put('/review/articles/frequency/batch', null, {
    params: { articleIds: articleIds.join(','), frequency }
  })
}

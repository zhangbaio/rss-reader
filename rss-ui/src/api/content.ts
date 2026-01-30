import request from './index'
import type { ArticleContent } from '@/types'

// 获取文章原文内容
export function getArticleContent(articleId: number): Promise<ArticleContent> {
  return request.get(`/article-content/${articleId}`)
}

// 重新抓取文章原文
export function fetchArticleContent(articleId: number): Promise<ArticleContent> {
  return request.post(`/article-content/${articleId}/fetch`)
}

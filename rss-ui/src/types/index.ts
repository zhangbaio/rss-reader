// 订阅源
export interface Feed {
  id: number
  feedUrl: string
  feedName: string
  feedDescription?: string
  feedLink?: string
  feedCategory?: string
  feedLanguage?: string
  feedImageUrl?: string
  lastFetchTime?: string
  fetchInterval?: number
  status?: number
  fetchErrorCount?: number
  lastErrorMsg?: string
  createTime: string
  updateTime: string
  unreadCount: number
}

// 文章
export interface Article {
  id: number
  feedId: number
  feedName?: string
  feedImageUrl?: string
  articleGuid?: string
  title: string
  link: string
  description: string
  author?: string
  pubDate: string
  category?: string
  enclosureUrl?: string
  enclosureType?: string
  imageUrl?: string
  isRead: number // 0: 未读, 1: 已读
  isFavorite: number // 0: 未收藏, 1: 已收藏
  createTime: string
}

// 分页参数
export interface PaginationParams {
  page: number
  size: number
}

// 文章查询参数
export interface ArticleQueryParams extends PaginationParams {
  feedId?: number
  isRead?: number
  isFavorite?: number
  keyword?: string
}

// 分页响应
export interface PageResult<T> {
  data: T[]
  total: number
  page: number
  size: number
  totalPages: number
}

// API响应
export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

// 添加订阅源参数
export interface AddFeedParams {
  feedUrl: string
  feedName?: string
  feedCategory?: string
}

// 更新订阅源参数
export interface UpdateFeedParams {
  feedName?: string
  feedCategory?: string
  fetchInterval?: number
}

// 标签
export interface Tag {
  id: number
  name: string
  color: string
}

// 划线标注
export interface Highlight {
  id: number
  articleId: number
  selectedText: string
  startOffset: number
  endOffset: number
  xpath?: string
  color: string
  note?: string
  tags: Tag[]
  createTime: string
}

// 创建划线参数
export interface CreateHighlightParams {
  articleId: number
  selectedText: string
  startOffset: number
  endOffset: number
  xpath?: string
  color: string
  note?: string
  tagNames?: string[]
}

// 更新划线参数
export interface UpdateHighlightParams {
  selectedText?: string
  startOffset?: number
  endOffset?: number
  xpath?: string
  color?: string
  note?: string
  tagNames?: string[]
}

// 文章笔记
export interface ArticleNote {
  id: number
  articleId: number
  noteContent: string
  createTime: string
  updateTime: string
}

// 文章内容
export interface ArticleContent {
  id: number
  articleId: number
  content: string
  wordCount: number
  fetchStatus: number
  fetchTime?: string
}

// Daily Review 高亮项（包含文章信息）
export interface ReviewHighlight extends Highlight {
  article?: {
    id: number
    title: string
    link: string
    feedName?: string
    feedImageUrl?: string
  }
  reviewCount: number
  lastReviewTime?: string
  masteryLevel: number // 0: 新的, 1: 学习中, 2: 已掌握
}

// Daily Review 操作类型
export type ReviewAction = 'keep' | 'master' | 'feedback' | 'discard'

// Daily Review 配置
export interface ReviewConfig {
  dailyCount: number // 每日复习数量
  recency: number // 内容新旧程度 0-100
  includeNew: boolean // 是否包含新高亮
  includeMastered: boolean // 是否包含已掌握的
}

// 文章复习频率
export interface ArticleFrequency {
  articleId: number
  title: string
  author?: string
  feedName?: string
  feedImageUrl?: string
  highlightCount: number
  latestHighlightTime?: string
  frequency: number // 0: never, 1: less, 2: normally, 3: more
}

// Web Article (from URL)
export interface WebArticle {
  id: number
  url: string
  title: string
  description?: string
  content?: string
  author?: string
  domain?: string
  imageUrl?: string
  wordCount: number
  readingTime: number
  progress: number
  category: 'inbox' | 'later' | 'archive'
  language?: string
  isRead: number
  isFavorite: number
  savedTime: string
  createTime: string
  highlightCount?: number
}

// Web Article Query Params
export interface WebArticleQueryParams extends PaginationParams {
  category?: 'inbox' | 'later' | 'archive'
  isRead?: number
  isFavorite?: number
  keyword?: string
  sortBy?: 'savedTime' | 'title'
  sortOrder?: 'asc' | 'desc'
}

// Web Article Add Params
export interface AddWebArticleParams {
  url: string
  title?: string
  category?: 'inbox' | 'later' | 'archive'
}

// Category Counts
export interface CategoryCounts {
  inbox: number
  later: number
  archive: number
  all: number
}

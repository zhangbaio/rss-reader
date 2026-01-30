import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Article, ArticleQueryParams } from '@/types'
import * as articleApi from '@/api/article'
import { useFeedStore } from './feed'

export const useArticleStore = defineStore('article', () => {
  const articles = ref<Article[]>([])
  const currentArticle = ref<Article | null>(null)
  const loading = ref(false)
  const hasMore = ref(true)
  const total = ref(0)

  const queryParams = ref<ArticleQueryParams>({
    page: 1,
    size: 20,
    feedId: undefined,
    isRead: undefined,
    isFavorite: undefined
  })

  const unreadArticles = computed(() => {
    return articles.value.filter(a => a.isRead === 0)
  })

  async function fetchArticles(reset = false) {
    if (reset) {
      queryParams.value.page = 1
      articles.value = []
      hasMore.value = true
    }

    if (!hasMore.value) return

    loading.value = true
    try {
      const response = await articleApi.getArticles(queryParams.value)
      if (reset) {
        articles.value = response.data
      } else {
        articles.value.push(...response.data)
      }
      total.value = response.total
      hasMore.value = queryParams.value.page < response.totalPages
      queryParams.value.page++
    } finally {
      loading.value = false
    }
  }

  async function fetchArticle(id: number) {
    loading.value = true
    try {
      currentArticle.value = await articleApi.getArticle(id)
      // 自动标记为已读
      if (currentArticle.value && currentArticle.value.isRead === 0) {
        await markAsRead(id)
      }
    } finally {
      loading.value = false
    }
  }

  async function markAsRead(id: number) {
    await articleApi.markAsRead(id)
    const article = articles.value.find(a => a.id === id)
    if (article && article.isRead === 0) {
      article.isRead = 1
      const feedStore = useFeedStore()
      feedStore.decreaseUnreadCount(article.feedId)
    }
    if (currentArticle.value?.id === id) {
      currentArticle.value.isRead = 1
    }
  }

  async function markAsUnread(id: number) {
    await articleApi.markAsUnread(id)
    const article = articles.value.find(a => a.id === id)
    if (article && article.isRead === 1) {
      article.isRead = 0
      const feedStore = useFeedStore()
      feedStore.increaseUnreadCount(article.feedId)
    }
    if (currentArticle.value?.id === id) {
      currentArticle.value.isRead = 0
    }
  }

  async function markAllAsRead() {
    const unreadIds = articles.value.filter(a => a.isRead === 0).map(a => a.id)
    if (unreadIds.length === 0) return

    await articleApi.batchMarkAsRead(unreadIds)
    const feedStore = useFeedStore()
    articles.value.forEach(article => {
      if (article.isRead === 0) {
        article.isRead = 1
        feedStore.decreaseUnreadCount(article.feedId)
      }
    })
  }

  async function toggleFavorite(id: number) {
    await articleApi.toggleFavorite(id)
    const article = articles.value.find(a => a.id === id)
    if (article) {
      article.isFavorite = article.isFavorite === 1 ? 0 : 1
    }
    if (currentArticle.value?.id === id) {
      currentArticle.value.isFavorite = currentArticle.value.isFavorite === 1 ? 0 : 1
    }
  }

  function setFilter(filter: Partial<ArticleQueryParams>) {
    queryParams.value = { ...queryParams.value, ...filter, page: 1 }
  }

  function clearCurrentArticle() {
    currentArticle.value = null
  }

  return {
    articles,
    currentArticle,
    loading,
    hasMore,
    total,
    queryParams,
    unreadArticles,
    fetchArticles,
    fetchArticle,
    markAsRead,
    markAsUnread,
    markAllAsRead,
    toggleFavorite,
    setFilter,
    clearCurrentArticle
  }
})

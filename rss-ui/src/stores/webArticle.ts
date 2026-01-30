import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { WebArticle, WebArticleQueryParams, CategoryCounts } from '@/types'
import * as webArticleApi from '@/api/webArticle'

export const useWebArticleStore = defineStore('webArticle', () => {
  // State
  const articles = ref<WebArticle[]>([])
  const currentArticle = ref<WebArticle | null>(null)
  const loading = ref(false)
  const total = ref(0)
  const currentPage = ref(1)
  const pageSize = ref(20)
  const currentCategory = ref<'inbox' | 'later' | 'archive'>('inbox')
  const categoryCounts = ref<CategoryCounts>({
    inbox: 0,
    later: 0,
    archive: 0,
    all: 0
  })

  // Getters
  const hasMore = computed(() => articles.value.length < total.value)

  // Actions
  async function fetchArticles(params?: Partial<WebArticleQueryParams>) {
    loading.value = true
    try {
      const query: WebArticleQueryParams = {
        page: params?.page || currentPage.value,
        size: params?.size || pageSize.value,
        category: params?.category || currentCategory.value,
        ...params
      }
      const result = await webArticleApi.getWebArticles(query)
      if (query.page === 1) {
        articles.value = result.data
      } else {
        articles.value = [...articles.value, ...result.data]
      }
      total.value = result.total
      currentPage.value = query.page
    } catch (error) {
      console.error('Failed to fetch articles:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  async function fetchArticleById(id: number) {
    loading.value = true
    try {
      currentArticle.value = await webArticleApi.getWebArticle(id)
      return currentArticle.value
    } catch (error) {
      console.error('Failed to fetch article:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  async function addArticle(url: string, title?: string, category?: 'inbox' | 'later' | 'archive') {
    loading.value = true
    try {
      const article = await webArticleApi.addWebArticle({ url, title, category })
      // Add to beginning of list if in same category
      if (!category || category === currentCategory.value) {
        articles.value = [article, ...articles.value]
        total.value += 1
      }
      // Update counts
      await fetchCategoryCounts()
      return article
    } catch (error) {
      console.error('Failed to add article:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  async function updateArticleCategory(id: number, category: 'inbox' | 'later' | 'archive') {
    try {
      await webArticleApi.updateCategory(id, category)
      // Remove from current list if category changed
      const index = articles.value.findIndex(a => a.id === id)
      if (index !== -1 && articles.value[index].category !== category) {
        articles.value[index].category = category
        if (currentCategory.value !== category) {
          articles.value.splice(index, 1)
          total.value -= 1
        }
      }
      // Update counts
      await fetchCategoryCounts()
    } catch (error) {
      console.error('Failed to update category:', error)
      throw error
    }
  }

  async function markArticleAsRead(id: number) {
    try {
      await webArticleApi.markAsRead(id)
      const article = articles.value.find(a => a.id === id)
      if (article) {
        article.isRead = 1
      }
      if (currentArticle.value?.id === id) {
        currentArticle.value.isRead = 1
      }
    } catch (error) {
      console.error('Failed to mark as read:', error)
      throw error
    }
  }

  async function markArticleAsUnread(id: number) {
    try {
      await webArticleApi.markAsUnread(id)
      const article = articles.value.find(a => a.id === id)
      if (article) {
        article.isRead = 0
      }
      if (currentArticle.value?.id === id) {
        currentArticle.value.isRead = 0
      }
    } catch (error) {
      console.error('Failed to mark as unread:', error)
      throw error
    }
  }

  async function toggleArticleFavorite(id: number) {
    try {
      await webArticleApi.toggleFavorite(id)
      const article = articles.value.find(a => a.id === id)
      if (article) {
        article.isFavorite = article.isFavorite === 1 ? 0 : 1
      }
      if (currentArticle.value?.id === id) {
        currentArticle.value.isFavorite = currentArticle.value.isFavorite === 1 ? 0 : 1
      }
    } catch (error) {
      console.error('Failed to toggle favorite:', error)
      throw error
    }
  }

  async function updateArticleProgress(id: number, progress: number) {
    try {
      await webArticleApi.updateProgress(id, progress)
      const article = articles.value.find(a => a.id === id)
      if (article) {
        article.progress = progress
      }
      if (currentArticle.value?.id === id) {
        currentArticle.value.progress = progress
      }
    } catch (error) {
      console.error('Failed to update progress:', error)
      throw error
    }
  }

  async function deleteArticle(id: number) {
    try {
      await webArticleApi.deleteWebArticle(id)
      const index = articles.value.findIndex(a => a.id === id)
      if (index !== -1) {
        articles.value.splice(index, 1)
        total.value -= 1
      }
      if (currentArticle.value?.id === id) {
        currentArticle.value = null
      }
      // Update counts
      await fetchCategoryCounts()
    } catch (error) {
      console.error('Failed to delete article:', error)
      throw error
    }
  }

  async function fetchCategoryCounts() {
    try {
      categoryCounts.value = await webArticleApi.getCategoryCounts()
    } catch (error) {
      console.error('Failed to fetch category counts:', error)
    }
  }

  function setCategory(category: 'inbox' | 'later' | 'archive') {
    currentCategory.value = category
    currentPage.value = 1
    articles.value = []
  }

  function loadMore() {
    if (hasMore.value && !loading.value) {
      fetchArticles({ page: currentPage.value + 1 })
    }
  }

  function reset() {
    articles.value = []
    currentArticle.value = null
    total.value = 0
    currentPage.value = 1
    currentCategory.value = 'inbox'
  }

  return {
    // State
    articles,
    currentArticle,
    loading,
    total,
    currentPage,
    pageSize,
    currentCategory,
    categoryCounts,
    // Getters
    hasMore,
    // Actions
    fetchArticles,
    fetchArticleById,
    addArticle,
    updateArticleCategory,
    markArticleAsRead,
    markArticleAsUnread,
    toggleArticleFavorite,
    updateArticleProgress,
    deleteArticle,
    fetchCategoryCounts,
    setCategory,
    loadMore,
    reset
  }
})

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Feed, AddFeedParams, UpdateFeedParams } from '@/types'
import * as feedApi from '@/api/feed'

export const useFeedStore = defineStore('feed', () => {
  const feeds = ref<Feed[]>([])
  const currentFeedId = ref<number | null>(null)
  const loading = ref(false)

  const currentFeed = computed(() => {
    if (!currentFeedId.value) return null
    return feeds.value.find(f => f.id === currentFeedId.value) || null
  })

  const totalUnreadCount = computed(() => {
    return feeds.value.reduce((sum, feed) => sum + (feed.unreadCount || 0), 0)
  })

  // 按分类分组订阅源
  const feedsByCategory = computed(() => {
    const map = new Map<string, Feed[]>()
    feeds.value.forEach(feed => {
      const category = feed.feedCategory || '未分类'
      if (!map.has(category)) {
        map.set(category, [])
      }
      map.get(category)!.push(feed)
    })
    return map
  })

  // 获取所有分类
  const categories = computed(() => {
    const cats = new Set<string>()
    feeds.value.forEach(feed => {
      if (feed.feedCategory) {
        cats.add(feed.feedCategory)
      }
    })
    return Array.from(cats)
  })

  async function fetchFeeds() {
    loading.value = true
    try {
      const result = await feedApi.getFeeds()
      feeds.value = result.data
    } finally {
      loading.value = false
    }
  }

  async function addFeed(params: AddFeedParams) {
    const feedId = await feedApi.addFeed(params)
    await fetchFeeds()
    return feedId
  }

  async function updateFeed(id: number, params: UpdateFeedParams) {
    await feedApi.updateFeed(id, params)
    await fetchFeeds()
  }

  async function deleteFeed(id: number) {
    await feedApi.deleteFeed(id)
    feeds.value = feeds.value.filter(f => f.id !== id)
    if (currentFeedId.value === id) {
      currentFeedId.value = null
    }
  }

  async function refreshFeed(id: number) {
    await feedApi.refreshFeed(id)
    await fetchFeeds()
  }

  async function refreshAllFeeds() {
    loading.value = true
    try {
      await feedApi.refreshAllFeeds()
      await fetchFeeds()
    } finally {
      loading.value = false
    }
  }

  function setCurrentFeed(id: number | null) {
    currentFeedId.value = id
  }

  function decreaseUnreadCount(feedId: number) {
    const feed = feeds.value.find(f => f.id === feedId)
    if (feed && feed.unreadCount > 0) {
      feed.unreadCount--
    }
  }

  function increaseUnreadCount(feedId: number) {
    const feed = feeds.value.find(f => f.id === feedId)
    if (feed) {
      feed.unreadCount++
    }
  }

  return {
    feeds,
    currentFeedId,
    currentFeed,
    totalUnreadCount,
    feedsByCategory,
    categories,
    loading,
    fetchFeeds,
    addFeed,
    updateFeed,
    deleteFeed,
    refreshFeed,
    refreshAllFeeds,
    setCurrentFeed,
    decreaseUnreadCount,
    increaseUnreadCount
  }
})

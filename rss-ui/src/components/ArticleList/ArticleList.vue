<template>
  <div class="article-list">
    <div class="list-header">
      <div class="header-left">
        <h2 class="list-title">{{ title }}</h2>
        <div class="status-tabs">
          <span
            class="status-tab"
            :class="{ active: filterStatus === 'unseen' }"
            @click="filterStatus = 'unseen'"
          >
            Unseen
            <span class="tab-count" v-if="unseenCount > 0">{{ unseenCount }}</span>
          </span>
          <span
            class="status-tab"
            :class="{ active: filterStatus === 'seen' }"
            @click="filterStatus = 'seen'"
          >
            Seen
          </span>
        </div>
      </div>
      <div class="header-right">
        <div class="sort-dropdown">
          <button class="sort-btn" @click="showSortMenu = !showSortMenu">
            <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="4" y1="6" x2="16" y2="6"/>
              <line x1="4" y1="12" x2="12" y2="12"/>
              <line x1="4" y1="18" x2="8" y2="18"/>
              <polyline points="15 15 18 18 21 15"/>
            </svg>
            <span>{{ sortByLabel }}</span>
            <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="6 9 12 15 18 9"/>
            </svg>
          </button>
          <div class="sort-menu" v-if="showSortMenu" @click.stop>
            <div class="sort-section">
              <div class="sort-section-title">Sort by</div>
              <div
                class="sort-option"
                :class="{ active: sortBy === 'pubDate' }"
                @click="setSortBy('pubDate')"
              >
                <svg v-if="sortBy === 'pubDate'" viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline points="20 6 9 17 4 12"/>
                </svg>
                <span>Date published</span>
              </div>
              <div
                class="sort-option"
                :class="{ active: sortBy === 'createTime' }"
                @click="setSortBy('createTime')"
              >
                <svg v-if="sortBy === 'createTime'" viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline points="20 6 9 17 4 12"/>
                </svg>
                <span>Date added</span>
              </div>
              <div
                class="sort-option"
                :class="{ active: sortBy === 'title' }"
                @click="setSortBy('title')"
              >
                <svg v-if="sortBy === 'title'" viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline points="20 6 9 17 4 12"/>
                </svg>
                <span>Title</span>
              </div>
              <div
                class="sort-option"
                :class="{ active: sortBy === 'author' }"
                @click="setSortBy('author')"
              >
                <svg v-if="sortBy === 'author'" viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline points="20 6 9 17 4 12"/>
                </svg>
                <span>Author</span>
              </div>
              <div
                class="sort-option"
                :class="{ active: sortBy === 'feedName' }"
                @click="setSortBy('feedName')"
              >
                <svg v-if="sortBy === 'feedName'" viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline points="20 6 9 17 4 12"/>
                </svg>
                <span>Feed source</span>
              </div>
            </div>
            <div class="sort-divider"></div>
            <div class="sort-section">
              <div class="sort-section-title">Order by</div>
              <div
                class="sort-option"
                :class="{ active: sortOrder === 'desc' }"
                @click="setSortOrder('desc')"
              >
                <svg v-if="sortOrder === 'desc'" viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline points="20 6 9 17 4 12"/>
                </svg>
                <span>Recent → Old</span>
              </div>
              <div
                class="sort-option"
                :class="{ active: sortOrder === 'asc' }"
                @click="setSortOrder('asc')"
              >
                <svg v-if="sortOrder === 'asc'" viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline points="20 6 9 17 4 12"/>
                </svg>
                <span>Old → Recent</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div
      class="list-content"
      ref="listRef"
      v-loading="articleStore.loading && articleStore.articles.length === 0"
    >
      <div v-if="filteredArticles.length === 0 && !articleStore.loading" class="empty-state">
        <svg viewBox="0 0 24 24" width="48" height="48" fill="none" stroke="currentColor" stroke-width="1.5">
          <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
          <polyline points="14 2 14 8 20 8"/>
        </svg>
        <p v-if="filterStatus === 'unseen'">No unseen articles</p>
        <p v-else>No seen articles</p>
      </div>

      <div class="articles-rows">
        <ArticleRow
          v-for="article in sortedArticles"
          :key="article.id"
          :article="article"
          @click="handleSelect(article)"
          @mouseenter="handleHover(article)"
        />
      </div>

      <div v-if="articleStore.loading && articleStore.articles.length > 0" class="loading-more">
        <div class="spinner"></div>
        <span>Loading...</span>
      </div>

      <div v-if="!articleStore.hasMore && articleStore.articles.length > 0" class="no-more">
        No more articles
      </div>
    </div>

    <!-- Click outside to close menu -->
    <div class="overlay" v-if="showSortMenu" @click="showSortMenu = false"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { useArticleStore } from '@/stores/article'
import type { Article } from '@/types'
import ArticleRow from './ArticleRow.vue'

type SortField = 'pubDate' | 'createTime' | 'title' | 'author' | 'feedName'
type SortOrder = 'asc' | 'desc'

const props = defineProps<{
  title: string
  feedId?: number
}>()

const emit = defineEmits<{
  (e: 'select', article: Article): void
  (e: 'hover', article: Article | null): void
}>()

const articleStore = useArticleStore()

const listRef = ref<HTMLElement | null>(null)
const filterStatus = ref('unseen')
const showSortMenu = ref(false)
const sortBy = ref<SortField>('pubDate')
const sortOrder = ref<SortOrder>('desc')

const sortByLabel = computed(() => {
  const labels: Record<SortField, string> = {
    pubDate: 'Date published',
    createTime: 'Date added',
    title: 'Title',
    author: 'Author',
    feedName: 'Feed source'
  }
  return labels[sortBy.value]
})

const filteredArticles = computed(() => {
  if (filterStatus.value === 'unseen') {
    return articleStore.articles.filter(a => a.isRead === 0)
  }
  return articleStore.articles.filter(a => a.isRead === 1)
})

const sortedArticles = computed(() => {
  const articles = [...filteredArticles.value]

  articles.sort((a, b) => {
    let valueA: string | number | undefined
    let valueB: string | number | undefined

    switch (sortBy.value) {
      case 'pubDate':
        valueA = new Date(a.pubDate).getTime()
        valueB = new Date(b.pubDate).getTime()
        break
      case 'createTime':
        valueA = new Date(a.createTime).getTime()
        valueB = new Date(b.createTime).getTime()
        break
      case 'title':
        valueA = a.title.toLowerCase()
        valueB = b.title.toLowerCase()
        break
      case 'author':
        valueA = (a.author || '').toLowerCase()
        valueB = (b.author || '').toLowerCase()
        break
      case 'feedName':
        valueA = (a.feedName || '').toLowerCase()
        valueB = (b.feedName || '').toLowerCase()
        break
    }

    if (valueA === undefined || valueA === '') return 1
    if (valueB === undefined || valueB === '') return -1

    let result = 0
    if (valueA < valueB) result = -1
    else if (valueA > valueB) result = 1

    return sortOrder.value === 'desc' ? -result : result
  })

  return articles
})

const unseenCount = computed(() => {
  return articleStore.articles.filter(a => a.isRead === 0).length
})

watch(
  () => props.feedId,
  () => {
    articleStore.setFilter({
      feedId: props.feedId,
      isRead: filterStatus.value === 'unseen' ? 0 : 1
    })
    articleStore.fetchArticles(true)
  },
  { immediate: true }
)

watch(filterStatus, () => {
  articleStore.setFilter({
    isRead: filterStatus.value === 'unseen' ? 0 : 1
  })
  articleStore.fetchArticles(true)
})

function handleSelect(article: Article) {
  emit('select', article)
}

function handleHover(article: Article) {
  emit('hover', article)
}

function setSortBy(sort: SortField) {
  sortBy.value = sort
}

function setSortOrder(order: SortOrder) {
  sortOrder.value = order
}

function handleScroll() {
  if (!listRef.value) return

  const { scrollTop, scrollHeight, clientHeight } = listRef.value
  if (scrollHeight - scrollTop - clientHeight < 100) {
    if (!articleStore.loading && articleStore.hasMore) {
      articleStore.fetchArticles()
    }
  }
}

onMounted(() => {
  listRef.value?.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  listRef.value?.removeEventListener('scroll', handleScroll)
})
</script>

<style lang="scss" scoped>
.article-list {
  display: flex;
  flex-direction: column;
  height: 100%;
  background-color: var(--bg-content);
}

.list-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  border-bottom: 1px solid var(--border-color);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 24px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.list-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
  padding: 16px 0;
}

.status-tabs {
  display: flex;
  gap: 24px;
  height: 100%;
}

.status-tab {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 16px 0;
  font-size: 14px;
  color: var(--text-muted);
  cursor: pointer;
  border-bottom: 2px solid transparent;
  margin-bottom: -1px;
  transition: all 0.2s;

  &:hover {
    color: var(--text-secondary);
  }

  &.active {
    color: var(--text-primary);
    font-weight: 500;
    border-bottom-color: var(--primary-color);
  }

  .tab-count {
    padding: 2px 8px;
    background-color: var(--primary-color);
    color: white;
    border-radius: 10px;
    font-size: 11px;
    font-weight: 600;
  }
}

.sort-dropdown {
  position: relative;
}

.sort-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  border: 1px solid var(--border-color);
  background: var(--bg-primary);
  color: var(--text-secondary);
  font-size: 13px;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.2s;

  &:hover {
    border-color: var(--text-secondary);
  }
}

.sort-menu {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 4px;
  min-width: 200px;
  background-color: #ffffff;
  border: 1px solid #e5e5e5;
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2), 0 0 0 1px rgba(0, 0, 0, 0.05);
  z-index: 100;
  overflow: hidden;
}

.sort-section {
  padding: 8px 0;
}

.sort-section-title {
  padding: 8px 16px;
  font-size: 12px;
  font-weight: 500;
  color: #999;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.sort-divider {
  height: 1px;
  background-color: #e5e5e5;
  margin: 4px 0;
}

.sort-option {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 16px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  transition: all 0.15s;

  svg {
    flex-shrink: 0;
    color: #4a90d9;
  }

  span {
    flex: 1;
  }

  &:hover {
    background-color: #f5f5f5;
    color: #1a1a1a;
  }

  &.active {
    color: #1a1a1a;
    font-weight: 500;
  }
}

.list-content {
  flex: 1;
  overflow-y: auto;
  padding: 8px 0;
}

.articles-rows {
  display: flex;
  flex-direction: column;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 300px;
  color: var(--text-muted);

  svg {
    opacity: 0.5;
    margin-bottom: 16px;
  }

  p {
    margin: 0;
    font-size: 14px;
  }
}

.loading-more,
.no-more {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 24px;
  color: var(--text-muted);
  font-size: 14px;
}

.spinner {
  width: 20px;
  height: 20px;
  border: 2px solid var(--border-color);
  border-top-color: var(--primary-color);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 50;
}
</style>

<template>
  <div class="review-config-page">
    <!-- 顶部导航栏 -->
    <header class="top-nav">
      <div class="nav-left">
        <div class="logo">
          <span class="logo-icon">R</span>
        </div>
        <nav class="nav-links">
          <a href="#" class="nav-link">Connect & Sync</a>
          <a href="#" class="nav-link">Browse</a>
          <a href="#" class="nav-link">Chat</a>
          <a href="#" class="nav-link">Resources</a>
          <router-link to="/rss" class="nav-link">Reader</router-link>
        </nav>
      </div>
      <div class="nav-right">
        <div class="nav-icon">
          <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="11" cy="11" r="8"/>
            <path d="M21 21l-4.35-4.35"/>
          </svg>
        </div>
        <div class="nav-icon">
          <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
            <path d="M13.73 21a2 2 0 0 1-3.46 0"/>
          </svg>
        </div>
        <div class="nav-icon user">
          <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="8" r="4"/>
            <path d="M4 20c0-4 4-6 8-6s8 2 8 6"/>
          </svg>
        </div>
      </div>
    </header>

    <!-- 主内容 -->
    <main class="main-content">
      <h1 class="page-title">Configure</h1>

      <!-- 标签页导航 -->
      <div class="tabs-nav">
        <button
          v-for="tab in tabs"
          :key="tab.key"
          class="tab-btn"
          :class="{ active: activeTab === tab.key }"
          @click="activeTab = tab.key"
        >
          {{ tab.label }}
        </button>
        <div class="tabs-right">
          <input
            v-if="activeTab === 'frequency'"
            type="text"
            class="search-input"
            placeholder="Search Articles"
            v-model="searchKeyword"
            @input="debouncedSearch"
          />
          <button class="next-btn" v-if="activeTab === 'settings'" @click="activeTab = 'frequency'">
            Next
            <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M9 18l6-6-6-6"/>
            </svg>
          </button>
        </div>
      </div>

      <!-- Review Settings 标签页 -->
      <div class="tab-content" v-if="activeTab === 'settings'">
        <div class="settings-card">
          <p class="settings-desc">
            Adjust the number and recency of highlights in your Daily Review
          </p>

          <!-- 每日高亮数量 -->
          <div class="setting-item">
            <label class="setting-label">Highlights Per Day</label>
            <div class="slider-container">
              <input
                type="range"
                class="slider"
                min="1"
                max="15"
                :value="config.dailyCount"
                @input="updateDailyCount"
              />
              <div class="slider-labels">
                <span>1</span>
                <span class="current-value">{{ config.dailyCount }}</span>
                <span>15</span>
              </div>
            </div>
          </div>

          <!-- 内容新旧程度 -->
          <div class="setting-item">
            <label class="setting-label">
              Highlight Recency
              <span class="info-icon" title="Controls whether older or newer highlights are shown more frequently">
                <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="12" cy="12" r="10"/>
                  <path d="M12 16v-4M12 8h.01"/>
                </svg>
              </span>
            </label>
            <div class="slider-container">
              <input
                type="range"
                class="slider"
                min="0"
                max="100"
                :value="config.recency"
                @input="updateRecency"
              />
              <div class="slider-labels">
                <span>Older</span>
                <span>Newer</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Frequency Tuning 标签页 -->
      <div class="tab-content" v-if="activeTab === 'frequency'">
        <!-- 全局频率设置 -->
        <div class="frequency-header">
          <span class="frequency-label">Set the frequency of resurfacing highlights for</span>
          <select class="frequency-select" v-model="filterType">
            <option value="articles">Articles</option>
          </select>
          <div class="global-slider">
            <input
              type="range"
              class="slider small"
              min="0"
              max="3"
              :value="globalFrequency"
              @input="updateGlobalFrequency"
            />
            <div class="frequency-labels">
              <span>Never</span>
              <span>Less</span>
              <span>Normally</span>
              <span>More</span>
            </div>
          </div>
          <span class="info-icon">
            <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10"/>
              <path d="M12 16v-4M12 8h.01"/>
            </svg>
          </span>
        </div>

        <!-- 文章列表 -->
        <div class="articles-table">
          <div class="table-header">
            <div class="col-title sortable" @click="sortBy('title')">
              Title
              <svg viewBox="0 0 24 24" width="12" height="12" fill="currentColor">
                <path d="M7 10l5 5 5-5z"/>
              </svg>
            </div>
            <div class="col-author sortable" @click="sortBy('author')">
              Author
              <svg viewBox="0 0 24 24" width="12" height="12" fill="currentColor">
                <path d="M7 10l5 5 5-5z"/>
              </svg>
            </div>
            <div class="col-highlights sortable" @click="sortBy('highlightCount')">
              Highlights
              <svg viewBox="0 0 24 24" width="12" height="12" fill="currentColor">
                <path d="M7 10l5 5 5-5z"/>
              </svg>
            </div>
            <div class="col-date">
              <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
                <line x1="16" y1="2" x2="16" y2="6"/>
                <line x1="8" y1="2" x2="8" y2="6"/>
                <line x1="3" y1="10" x2="21" y2="10"/>
              </svg>
            </div>
            <div class="col-frequency">
              Review Frequency
              <span class="info-icon small">
                <svg viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="12" cy="12" r="10"/>
                  <path d="M12 16v-4M12 8h.01"/>
                </svg>
              </span>
            </div>
          </div>

          <div class="table-body">
            <div
              v-for="article in sortedArticles"
              :key="article.articleId"
              class="table-row"
            >
              <div class="col-title">
                <img
                  v-if="article.feedImageUrl"
                  :src="article.feedImageUrl"
                  class="feed-icon"
                  @error="handleIconError"
                />
                <div class="feed-icon-placeholder" v-else>
                  <svg viewBox="0 0 24 24" width="12" height="12" fill="currentColor">
                    <path d="M4 11a9 9 0 0 1 9 9M4 4a16 16 0 0 1 16 16M5 19a1 1 0 1 1-2 0 1 1 0 0 1 2 0z"/>
                  </svg>
                </div>
                <div class="title-info">
                  <span class="article-title">{{ article.title }}</span>
                  <span class="article-author" v-if="article.author">{{ article.author }}</span>
                </div>
              </div>
              <div class="col-author">{{ article.author || '-' }}</div>
              <div class="col-highlights">{{ article.highlightCount }}</div>
              <div class="col-date">{{ formatDate(article.latestHighlightTime) }}</div>
              <div class="col-frequency">
                <input
                  type="range"
                  class="slider row-slider"
                  min="0"
                  max="3"
                  :value="article.frequency"
                  @input="(e) => updateArticleFrequency(article.articleId, Number((e.target as HTMLInputElement).value))"
                />
              </div>
            </div>

            <div v-if="articleFrequencies.length === 0" class="empty-state">
              <p>No articles with highlights found</p>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useReviewStore } from '@/stores/review'

const reviewStore = useReviewStore()
const { config, articleFrequencies } = storeToRefs(reviewStore)

const activeTab = ref('settings')
const searchKeyword = ref('')
const filterType = ref('articles')
const globalFrequency = ref(2)
const sortField = ref('latestHighlightTime')
const sortOrder = ref<'asc' | 'desc'>('desc')

const tabs = [
  { key: 'settings', label: 'Review Settings' },
  { key: 'frequency', label: 'Frequency Tuning' }
]

const sortedArticles = computed(() => {
  const articles = [...articleFrequencies.value]
  articles.sort((a, b) => {
    let aVal: string | number = a[sortField.value as keyof typeof a] as string | number
    let bVal: string | number = b[sortField.value as keyof typeof b] as string | number

    if (sortField.value === 'latestHighlightTime') {
      aVal = aVal ? new Date(aVal as string).getTime() : 0
      bVal = bVal ? new Date(bVal as string).getTime() : 0
    }

    if (sortOrder.value === 'asc') {
      return aVal < bVal ? -1 : aVal > bVal ? 1 : 0
    } else {
      return aVal > bVal ? -1 : aVal < bVal ? 1 : 0
    }
  })
  return articles
})

onMounted(async () => {
  await reviewStore.fetchConfig()
  await reviewStore.fetchArticleFrequencies()
})

function updateDailyCount(e: Event) {
  const value = Number((e.target as HTMLInputElement).value)
  reviewStore.updateConfig({ dailyCount: value })
}

function updateRecency(e: Event) {
  const value = Number((e.target as HTMLInputElement).value)
  reviewStore.updateConfig({ recency: value })
}

function updateGlobalFrequency(e: Event) {
  const value = Number((e.target as HTMLInputElement).value)
  globalFrequency.value = value
  // 批量更新所有文章
  const articleIds = articleFrequencies.value.map(a => a.articleId)
  if (articleIds.length > 0) {
    reviewStore.batchUpdateFrequency(articleIds, value)
  }
}

function updateArticleFrequency(articleId: number, frequency: number) {
  reviewStore.updateArticleFrequency(articleId, frequency)
}

let searchTimeout: ReturnType<typeof setTimeout>
function debouncedSearch() {
  clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => {
    reviewStore.fetchArticleFrequencies(searchKeyword.value)
  }, 300)
}

function sortBy(field: string) {
  if (sortField.value === field) {
    sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc'
  } else {
    sortField.value = field
    sortOrder.value = 'desc'
  }
}

function formatDate(dateStr?: string) {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleDateString('en-US', {
    month: 'numeric',
    day: 'numeric',
    year: 'numeric'
  })
}

function handleIconError(e: Event) {
  const img = e.target as HTMLImageElement
  img.style.display = 'none'
}
</script>

<style lang="scss" scoped>
.review-config-page {
  min-height: 100vh;
  background-color: #fff;
}

// 顶部导航栏
.top-nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 24px;
  background-color: #fff;
  border-bottom: 1px solid #eee;
}

.nav-left {
  display: flex;
  align-items: center;
  gap: 32px;
}

.logo {
  .logo-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 32px;
    height: 32px;
    background-color: #1a1a1a;
    color: #fff;
    font-weight: bold;
    font-size: 18px;
    border-radius: 6px;
  }
}

.nav-links {
  display: flex;
  gap: 24px;
}

.nav-link {
  color: #333;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: color 0.2s;

  &:hover {
    color: #5c7cfa;
  }
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.nav-icon {
  color: #666;
  cursor: pointer;
  transition: color 0.2s;

  &:hover {
    color: #333;
  }
}

// 主内容
.main-content {
  max-width: 1000px;
  margin: 0 auto;
  padding: 40px 24px;
}

.page-title {
  font-size: 48px;
  font-weight: 400;
  font-style: italic;
  font-family: Georgia, serif;
  color: #1a1a1a;
  margin: 0 0 32px 0;
}

// 标签页导航
.tabs-nav {
  display: flex;
  align-items: center;
  border-bottom: 1px solid #e8e8e8;
  margin-bottom: 32px;
}

.tab-btn {
  padding: 12px 0;
  margin-right: 32px;
  border: none;
  background: none;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  position: relative;
  transition: color 0.2s;

  &:hover {
    color: #333;
  }

  &.active {
    color: #333;
    font-weight: 500;

    &::after {
      content: '';
      position: absolute;
      left: 0;
      right: 0;
      bottom: -1px;
      height: 3px;
      background-color: #5c7cfa;
    }
  }
}

.tabs-right {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 16px;
}

.search-input {
  padding: 8px 12px;
  border: 1px solid #e8e8e8;
  border-radius: 6px;
  font-size: 14px;
  width: 200px;

  &:focus {
    outline: none;
    border-color: #5c7cfa;
  }
}

.next-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 16px;
  border: none;
  background: none;
  color: #5c7cfa;
  font-size: 14px;
  cursor: pointer;

  &:hover {
    color: #4c6ef5;
  }
}

// Settings 卡片
.settings-card {
  max-width: 600px;
  margin: 0 auto;
  padding: 40px;
  border: 1px solid #e8e8e8;
  border-radius: 12px;
}

.settings-desc {
  font-size: 16px;
  color: #666;
  margin: 0 0 32px 0;
}

.setting-item {
  margin-bottom: 32px;

  &:last-child {
    margin-bottom: 0;
  }
}

.setting-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
}

.info-icon {
  color: #999;
  cursor: help;

  &.small {
    width: 14px;
    height: 14px;
  }
}

.slider-container {
  width: 100%;
}

.slider {
  width: 100%;
  height: 6px;
  border-radius: 3px;
  background: #e8e8e8;
  outline: none;
  -webkit-appearance: none;

  &::-webkit-slider-thumb {
    -webkit-appearance: none;
    width: 20px;
    height: 20px;
    border-radius: 50%;
    background: #5c7cfa;
    cursor: pointer;
    border: 3px solid #fff;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  }

  &.small {
    width: 200px;
  }

  &.row-slider {
    width: 150px;
  }
}

.slider-labels {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
  font-size: 13px;
  color: #999;

  .current-value {
    color: #5c7cfa;
    font-weight: 600;
  }
}

// Frequency Tuning
.frequency-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.frequency-label {
  font-size: 14px;
  color: #666;
}

.frequency-select {
  padding: 8px 12px;
  border: 1px solid #5c7cfa;
  border-radius: 6px;
  color: #5c7cfa;
  background: #fff;
  font-size: 14px;
  cursor: pointer;
}

.global-slider {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.frequency-labels {
  display: flex;
  justify-content: space-between;
  font-size: 11px;
  color: #999;
  width: 200px;
}

// 文章表格
.articles-table {
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  overflow: hidden;
}

.table-header {
  display: grid;
  grid-template-columns: 2fr 1fr 100px 100px 180px;
  gap: 16px;
  padding: 12px 16px;
  background-color: #fafafa;
  border-bottom: 1px solid #e8e8e8;
  font-size: 13px;
  font-weight: 500;
  color: #666;
}

.sortable {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;

  &:hover {
    color: #333;
  }
}

.table-body {
  max-height: 500px;
  overflow-y: auto;
}

.table-row {
  display: grid;
  grid-template-columns: 2fr 1fr 100px 100px 180px;
  gap: 16px;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  align-items: center;

  &:last-child {
    border-bottom: none;
  }

  &:hover {
    background-color: #fafafa;
  }
}

.col-title {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}

.feed-icon {
  width: 28px;
  height: 28px;
  border-radius: 6px;
  object-fit: contain;
  flex-shrink: 0;
}

.feed-icon-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  background-color: #ff6b35;
  color: #fff;
  border-radius: 6px;
  flex-shrink: 0;
}

.title-info {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.article-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.article-author {
  font-size: 12px;
  color: #999;
}

.col-author,
.col-highlights,
.col-date {
  font-size: 14px;
  color: #666;
}

.col-frequency {
  display: flex;
  align-items: center;
}

.empty-state {
  padding: 48px;
  text-align: center;
  color: #999;
}

// 响应式
@media (max-width: 768px) {
  .nav-links {
    display: none;
  }

  .table-header,
  .table-row {
    grid-template-columns: 1fr 80px 120px;
  }

  .col-author,
  .col-date {
    display: none;
  }
}
</style>

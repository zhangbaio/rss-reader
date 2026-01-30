<template>
  <div class="articles-page">
    <!-- Header -->
    <header class="page-header">
      <div class="header-left">
        <h1>Articles</h1>
        <div class="tabs">
          <button
            class="tab"
            :class="{ active: currentTab === 'inbox' }"
            @click="switchTab('inbox')"
          >
            INBOX
            <span class="count" v-if="store.categoryCounts.inbox > 0">{{ store.categoryCounts.inbox }}</span>
          </button>
          <button
            class="tab"
            :class="{ active: currentTab === 'later' }"
            @click="switchTab('later')"
          >
            LATER
            <span class="count" v-if="store.categoryCounts.later > 0">{{ store.categoryCounts.later }}</span>
          </button>
          <button
            class="tab"
            :class="{ active: currentTab === 'archive' }"
            @click="switchTab('archive')"
          >
            ARCHIVE
            <span class="count" v-if="store.categoryCounts.archive > 0">{{ store.categoryCounts.archive }}</span>
          </button>
        </div>
      </div>
      <div class="header-right">
        <div class="sort-dropdown">
          <button class="sort-btn" @click="showSortMenu = !showSortMenu">
            <span>Date saved</span>
            <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="6 9 12 15 18 9"/>
            </svg>
          </button>
          <div class="sort-menu" v-if="showSortMenu" @click.stop>
            <div class="sort-option" :class="{ active: sortBy === 'savedTime' }" @click="setSortBy('savedTime')">
              Date saved
            </div>
            <div class="sort-option" :class="{ active: sortBy === 'title' }" @click="setSortBy('title')">
              Title
            </div>
          </div>
        </div>
      </div>
    </header>

    <!-- Article List -->
    <div class="articles-container">
      <div class="articles-list" v-if="store.articles.length > 0">
        <div
          class="article-item"
          v-for="article in store.articles"
          :key="article.id"
          :class="{ selected: selectedArticle?.id === article.id }"
          @click="selectArticle(article)"
          @contextmenu="showArticleDetail(article, $event)"
        >
          <div class="article-image" v-if="article.imageUrl">
            <img :src="article.imageUrl" :alt="article.title" />
          </div>
          <div class="article-image placeholder" v-else>
            <svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="1.5">
              <rect x="3" y="3" width="18" height="18" rx="2"/>
              <circle cx="8.5" cy="8.5" r="1.5"/>
              <path d="M21 15l-5-5L5 21"/>
            </svg>
          </div>
          <div class="article-content">
            <h3 class="article-title" :class="{ read: article.isRead === 1 }">
              {{ article.title }}
            </h3>
            <p class="article-description" v-if="article.description">
              {{ article.description }}
            </p>
            <div class="article-meta">
              <span class="domain">{{ article.domain }}</span>
              <span class="separator">·</span>
              <span class="author" v-if="article.author">{{ article.author }}</span>
              <span class="separator" v-if="article.author">·</span>
              <span class="reading-time">{{ article.readingTime }} min</span>
            </div>
          </div>
          <div class="article-actions">
            <button class="action-btn" @click.stop="toggleFavorite(article)" :class="{ active: article.isFavorite === 1 }">
              <svg viewBox="0 0 24 24" width="18" height="18" :fill="article.isFavorite === 1 ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2">
                <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
              </svg>
            </button>
            <div class="category-dropdown">
              <button class="action-btn" @click.stop="toggleCategoryMenu(article.id)">
                <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="12" cy="12" r="1"/>
                  <circle cx="19" cy="12" r="1"/>
                  <circle cx="5" cy="12" r="1"/>
                </svg>
              </button>
              <div class="category-menu" v-if="activeCategoryMenu === article.id" @click.stop>
                <div class="menu-item" @click="moveToCategory(article, 'inbox')">
                  <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
                    <polyline points="22 12 16 12 14 15 10 15 8 12 2 12"/>
                    <path d="M5.45 5.11L2 12v6a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-6l-3.45-6.89A2 2 0 0 0 16.76 4H7.24a2 2 0 0 0-1.79 1.11z"/>
                  </svg>
                  <span>Move to Inbox</span>
                  <svg v-if="article.category === 'inbox'" viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" class="check">
                    <polyline points="20 6 9 17 4 12"/>
                  </svg>
                </div>
                <div class="menu-item" @click="moveToCategory(article, 'later')">
                  <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="12" cy="12" r="10"/>
                    <polyline points="12 6 12 12 16 14"/>
                  </svg>
                  <span>Save for Later</span>
                  <svg v-if="article.category === 'later'" viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" class="check">
                    <polyline points="20 6 9 17 4 12"/>
                  </svg>
                </div>
                <div class="menu-item" @click="moveToCategory(article, 'archive')">
                  <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
                    <polyline points="21 8 21 21 3 21 3 8"/>
                    <rect x="1" y="3" width="22" height="5"/>
                    <line x1="10" y1="12" x2="14" y2="12"/>
                  </svg>
                  <span>Archive</span>
                  <svg v-if="article.category === 'archive'" viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" class="check">
                    <polyline points="20 6 9 17 4 12"/>
                  </svg>
                </div>
                <div class="menu-divider"></div>
                <div class="menu-item danger" @click="deleteArticle(article)">
                  <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
                    <polyline points="3 6 5 6 21 6"/>
                    <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                  </svg>
                  <span>Delete</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Load More -->
        <div class="load-more" v-if="store.hasMore">
          <button @click="loadMore" :disabled="store.loading">
            {{ store.loading ? 'Loading...' : 'Load More' }}
          </button>
        </div>
      </div>

      <!-- Empty State -->
      <div class="empty-state" v-else-if="!store.loading">
        <svg viewBox="0 0 24 24" width="48" height="48" fill="none" stroke="currentColor" stroke-width="1.5">
          <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
          <polyline points="14 2 14 8 20 8"/>
        </svg>
        <h3>No articles in {{ currentTab }}</h3>
        <p>Add articles by clicking the + button in the sidebar</p>
      </div>

      <!-- Loading State -->
      <div class="loading-state" v-if="store.loading && store.articles.length === 0">
        <div class="spinner"></div>
        <p>Loading articles...</p>
      </div>

      <!-- Article Detail Panel -->
      <div class="article-detail" v-if="selectedArticle">
        <div class="detail-header">
          <button class="close-btn" @click="selectedArticle = null">
            <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="18" y1="6" x2="6" y2="18"/>
              <line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
          </button>
        </div>
        <div class="detail-content">
          <div class="detail-section">
            <label>Title</label>
            <p>{{ selectedArticle.title }}</p>
          </div>
          <div class="detail-section">
            <label>Domain</label>
            <p>{{ selectedArticle.domain }}</p>
          </div>
          <div class="detail-section">
            <label>Saved</label>
            <p>{{ formatDate(selectedArticle.savedTime) }}</p>
          </div>
          <div class="detail-section" v-if="selectedArticle.author">
            <label>Author</label>
            <p>{{ selectedArticle.author }}</p>
          </div>
          <div class="detail-section">
            <label>Progress</label>
            <p>{{ selectedArticle.progress }}% ({{ selectedArticle.readingTime }} min left)</p>
          </div>
          <div class="detail-section" v-if="selectedArticle.language">
            <label>Language</label>
            <p>{{ selectedArticle.language }}</p>
          </div>
          <div class="detail-actions">
            <button class="read-btn" @click="openArticle(selectedArticle)">
              Read Article
              <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                <circle cx="12" cy="12" r="3"/>
              </svg>
            </button>
            <a :href="selectedArticle.url" target="_blank" class="open-btn secondary">
              Open Original
              <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6"/>
                <polyline points="15 3 21 3 21 9"/>
                <line x1="10" y1="14" x2="21" y2="3"/>
              </svg>
            </a>
          </div>
        </div>
      </div>
    </div>

    <!-- Click outside to close menus -->
    <div class="overlay" v-if="showSortMenu || activeCategoryMenu" @click="closeMenus"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useWebArticleStore } from '@/stores/webArticle'
import type { WebArticle } from '@/types'

const router = useRouter()
const store = useWebArticleStore()

const currentTab = ref<'inbox' | 'later' | 'archive'>('inbox')
const selectedArticle = ref<WebArticle | null>(null)
const showSortMenu = ref(false)
const sortBy = ref<'savedTime' | 'title'>('savedTime')
const activeCategoryMenu = ref<number | null>(null)

onMounted(async () => {
  await store.fetchCategoryCounts()
  await store.fetchArticles({ category: currentTab.value, page: 1 })
})

function switchTab(tab: 'inbox' | 'later' | 'archive') {
  currentTab.value = tab
  store.setCategory(tab)
  store.fetchArticles({ category: tab, page: 1 })
  selectedArticle.value = null
}

function selectArticle(article: WebArticle) {
  // Navigate to reader on click
  router.push(`/articles/${article.id}`)
}

function showArticleDetail(article: WebArticle, event: MouseEvent) {
  // Show detail panel on right-click or with modifier key
  event.preventDefault()
  selectedArticle.value = article
}

function setSortBy(sort: 'savedTime' | 'title') {
  sortBy.value = sort
  showSortMenu.value = false
  store.fetchArticles({ category: currentTab.value, page: 1, sortBy: sort })
}

function toggleCategoryMenu(articleId: number) {
  activeCategoryMenu.value = activeCategoryMenu.value === articleId ? null : articleId
}

async function moveToCategory(article: WebArticle, category: 'inbox' | 'later' | 'archive') {
  if (article.category !== category) {
    await store.updateArticleCategory(article.id, category)
  }
  activeCategoryMenu.value = null
}

async function toggleFavorite(article: WebArticle) {
  await store.toggleArticleFavorite(article.id)
}

async function deleteArticle(article: WebArticle) {
  if (confirm('Are you sure you want to delete this article?')) {
    await store.deleteArticle(article.id)
    if (selectedArticle.value?.id === article.id) {
      selectedArticle.value = null
    }
  }
  activeCategoryMenu.value = null
}

function loadMore() {
  store.loadMore()
}

function closeMenus() {
  showSortMenu.value = false
  activeCategoryMenu.value = null
}

function openArticle(article: WebArticle) {
  router.push(`/articles/${article.id}`)
}

function formatDate(dateStr: string) {
  const date = new Date(dateStr)
  const now = new Date()
  const diffMs = now.getTime() - date.getTime()
  const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24))

  if (diffDays === 0) {
    const diffHours = Math.floor(diffMs / (1000 * 60 * 60))
    if (diffHours === 0) {
      const diffMins = Math.floor(diffMs / (1000 * 60))
      return `${diffMins} min ago`
    }
    return `${diffHours} hours ago`
  } else if (diffDays === 1) {
    return 'Yesterday'
  } else if (diffDays < 7) {
    return `${diffDays} days ago`
  } else {
    return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })
  }
}
</script>

<style lang="scss" scoped>
.articles-page {
  display: flex;
  flex-direction: column;
  height: 100%;
  background-color: var(--bg-primary);
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  border-bottom: 1px solid var(--border-color);
  background-color: var(--bg-primary);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 24px;

  h1 {
    font-size: 20px;
    font-weight: 600;
    color: var(--text-primary);
    margin: 0;
  }
}

.tabs {
  display: flex;
  gap: 4px;
}

.tab {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border: none;
  background: none;
  color: var(--text-secondary);
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.2s;

  &:hover {
    background-color: var(--hover-color);
    color: var(--text-primary);
  }

  &.active {
    background-color: var(--hover-color);
    color: var(--text-primary);
  }

  .count {
    padding: 2px 6px;
    background-color: var(--bg-secondary);
    border-radius: 10px;
    font-size: 11px;
  }
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
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
  min-width: 150px;
  background: var(--bg-primary);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  z-index: 100;
  overflow: hidden;
}

.sort-option {
  padding: 10px 16px;
  font-size: 13px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.15s;

  &:hover {
    background-color: var(--hover-color);
    color: var(--text-primary);
  }

  &.active {
    color: var(--primary-color);
    font-weight: 500;
  }
}

.articles-container {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.articles-list {
  flex: 1;
  overflow-y: auto;
  padding: 16px 24px;
}

.article-item {
  display: flex;
  gap: 16px;
  padding: 16px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background-color: var(--hover-color);
  }

  &.selected {
    background-color: var(--hover-color);
  }
}

.article-image {
  flex-shrink: 0;
  width: 120px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  background-color: var(--bg-secondary);

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  &.placeholder {
    display: flex;
    align-items: center;
    justify-content: center;
    color: var(--text-muted);
  }
}

.article-content {
  flex: 1;
  min-width: 0;
}

.article-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 8px 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;

  &.read {
    color: var(--text-secondary);
  }
}

.article-description {
  font-size: 13px;
  color: var(--text-secondary);
  margin: 0 0 8px 0;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: var(--text-muted);

  .separator {
    color: var(--border-color);
  }
}

.article-actions {
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border: none;
  background: none;
  color: var(--text-muted);
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.2s;

  &:hover {
    background-color: var(--bg-secondary);
    color: var(--text-primary);
  }

  &.active {
    color: #e74c3c;
  }
}

.category-dropdown {
  position: relative;
}

.category-menu {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 4px;
  min-width: 180px;
  background: var(--bg-primary);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  z-index: 100;
  overflow: hidden;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  font-size: 13px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.15s;

  &:hover {
    background-color: var(--hover-color);
    color: var(--text-primary);
  }

  &.danger {
    color: #e74c3c;

    &:hover {
      background-color: rgba(231, 76, 60, 0.1);
    }
  }

  span {
    flex: 1;
  }

  .check {
    color: var(--primary-color);
  }
}

.menu-divider {
  height: 1px;
  background-color: var(--border-color);
  margin: 4px 0;
}

.load-more {
  display: flex;
  justify-content: center;
  padding: 24px;

  button {
    padding: 10px 24px;
    border: 1px solid var(--border-color);
    background: var(--bg-primary);
    color: var(--text-secondary);
    font-size: 14px;
    cursor: pointer;
    border-radius: 8px;
    transition: all 0.2s;

    &:hover:not(:disabled) {
      border-color: var(--primary-color);
      color: var(--primary-color);
    }

    &:disabled {
      cursor: not-allowed;
      opacity: 0.6;
    }
  }
}

.empty-state,
.loading-state {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--text-muted);
  padding: 48px;

  svg {
    margin-bottom: 16px;
    opacity: 0.5;
  }

  h3 {
    font-size: 16px;
    font-weight: 500;
    color: var(--text-secondary);
    margin: 0 0 8px 0;
  }

  p {
    font-size: 14px;
    margin: 0;
  }
}

.spinner {
  width: 32px;
  height: 32px;
  border: 3px solid var(--border-color);
  border-top-color: var(--primary-color);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.article-detail {
  width: 320px;
  border-left: 1px solid var(--border-color);
  background-color: var(--bg-secondary);
  display: flex;
  flex-direction: column;
}

.detail-header {
  display: flex;
  justify-content: flex-end;
  padding: 12px;
  border-bottom: 1px solid var(--border-color);
}

.close-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border: none;
  background: none;
  color: var(--text-muted);
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.2s;

  &:hover {
    background-color: var(--hover-color);
    color: var(--text-primary);
  }
}

.detail-content {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.detail-section {
  margin-bottom: 20px;

  label {
    display: block;
    font-size: 11px;
    font-weight: 600;
    color: var(--text-muted);
    text-transform: uppercase;
    letter-spacing: 0.5px;
    margin-bottom: 6px;
  }

  p {
    font-size: 14px;
    color: var(--text-primary);
    margin: 0;
    line-height: 1.5;
  }
}

.detail-actions {
  margin-top: 24px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.read-btn,
.open-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 10px 16px;
  background-color: var(--primary-color);
  color: white;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    opacity: 0.9;
  }

  &.secondary {
    background-color: transparent;
    border: 1px solid var(--border-color);
    color: var(--text-secondary);

    &:hover {
      border-color: var(--text-secondary);
      color: var(--text-primary);
    }
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

<template>
  <aside class="sidebar" :class="{ collapsed }">
    <!-- 顶部 Header -->
    <div class="sidebar-header">
      <div class="logo" v-show="!collapsed">
        <span class="logo-text">Readwise</span>
      </div>
      <div class="header-actions">
        <button class="icon-btn" @click="$emit('toggle')" title="Toggle sidebar">
          <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
            <rect x="3" y="3" width="18" height="18" rx="2"/>
            <line x1="9" y1="3" x2="9" y2="21"/>
          </svg>
        </button>
        <div class="add-btn-wrapper" v-show="!collapsed">
          <button class="icon-btn" @click="toggleAddMenu" title="Add new" ref="addBtnRef">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10"/>
              <line x1="12" y1="8" x2="12" y2="16"/>
              <line x1="8" y1="12" x2="16" y2="12"/>
            </svg>
          </button>
        </div>
      </div>
    </div>

    <!-- 添加菜单弹窗 - 使用 Teleport 渲染到 body -->
    <Teleport to="body">
      <div class="add-menu-overlay" v-if="showAddMenu" @click="showAddMenu = false"></div>
      <div
        class="add-menu"
        v-if="showAddMenu"
        :style="menuPosition"
        @click.stop
      >
        <div class="menu-section">
          <div class="menu-item" @click="handleAddUrl">
            <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"/>
              <path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"/>
            </svg>
            <span>URL</span>
            <span class="shortcut">A</span>
          </div>
          <div class="menu-item" @click="handleUpload">
            <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
              <polyline points="17 8 12 3 7 8"/>
              <line x1="12" y1="3" x2="12" y2="15"/>
            </svg>
            <span>Upload</span>
            <span class="shortcut">U</span>
          </div>
        </div>

        <div class="menu-divider"></div>

        <div class="menu-section">
          <div class="menu-item" @click="handleAddEmail">
            <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
              <polyline points="22,6 12,13 2,6"/>
            </svg>
            <span>Email</span>
          </div>
          <div class="menu-item" @click="handleAddRssFeed">
            <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M4 11a9 9 0 0 1 9 9"/>
              <path d="M4 4a16 16 0 0 1 16 16"/>
              <circle cx="5" cy="19" r="1"/>
            </svg>
            <span>RSS Feed</span>
            <span class="shortcut">Shift A</span>
          </div>
          <div class="menu-item" @click="handleAddTwitter">
            <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
              <path d="M23 3a10.9 10.9 0 0 1-3.14 1.53 4.48 4.48 0 0 0-7.86 3v1A10.66 10.66 0 0 1 3 4s-4 9 5 13a11.64 11.64 0 0 1-7 2c9 5 20 0 20-11.5a4.5 4.5 0 0 0-.08-.83A7.72 7.72 0 0 0 23 3z"/>
            </svg>
            <span>Twitter List</span>
            <span class="shortcut">Shift A</span>
          </div>
          <div class="menu-item" @click="handleAddYouTube">
            <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10"/>
              <polygon points="10 8 16 12 10 16 10 8" fill="currentColor"/>
            </svg>
            <span>YouTube Channel</span>
            <span class="shortcut">Shift A</span>
          </div>
        </div>

        <div class="menu-divider"></div>

        <div class="menu-section">
          <div class="menu-item" @click="handleConfigureIntegrations">
            <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
              <polyline points="14 2 14 8 20 8"/>
              <line x1="16" y1="13" x2="8" y2="13"/>
              <line x1="16" y1="17" x2="8" y2="17"/>
            </svg>
            <span>Configure integrations</span>
          </div>
          <div class="menu-item" @click="handleGetExtension">
            <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10"/>
              <circle cx="12" cy="12" r="4"/>
              <line x1="4.93" y1="4.93" x2="9.17" y2="9.17"/>
              <line x1="14.83" y1="14.83" x2="19.07" y2="19.07"/>
              <line x1="14.83" y1="9.17" x2="19.07" y2="4.93"/>
              <line x1="4.93" y1="19.07" x2="9.17" y2="14.83"/>
            </svg>
            <span>Get browser extension</span>
          </div>
          <div class="menu-item" @click="handleDownloadApps">
            <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="5" y="2" width="14" height="20" rx="2" ry="2"/>
              <line x1="12" y1="18" x2="12.01" y2="18"/>
            </svg>
            <span>Download apps</span>
          </div>
        </div>

        <div class="menu-divider"></div>

        <div class="menu-section">
          <div class="menu-item" @click="handleDiscoverDocuments">
            <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/>
              <path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/>
            </svg>
            <span>Discover new documents</span>
          </div>
          <div class="menu-item" @click="handleMoreOptions">
            <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
              <circle cx="5" cy="12" r="2"/>
              <circle cx="12" cy="12" r="2"/>
              <circle cx="19" cy="12" r="2"/>
            </svg>
            <span>More options</span>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- 导航区域 -->
    <nav class="sidebar-nav">
      <!-- Home -->
      <router-link to="/" class="nav-item" active-class="active" v-show="!collapsed">
        <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
          <polyline points="9 22 9 12 15 12 15 22"/>
        </svg>
        <span>Home</span>
      </router-link>

      <!-- Library Section -->
      <div class="nav-section">
        <div
          class="section-header clickable"
          :class="{ expanded: expandedSections.has('library') }"
          @click="toggleSection('library')"
          v-show="!collapsed"
        >
          <svg
            viewBox="0 0 24 24"
            width="12"
            height="12"
            fill="currentColor"
            class="arrow-icon"
            :class="{ rotated: expandedSections.has('library') }"
          >
            <path d="M9 18l6-6-6-6"/>
          </svg>
          <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="4" y1="6" x2="4" y2="18"/>
            <line x1="8" y1="6" x2="8" y2="18"/>
            <line x1="12" y1="6" x2="12" y2="18"/>
          </svg>
          <span>Library</span>
        </div>

        <div class="section-content" v-show="expandedSections.has('library') && !collapsed">
          <router-link to="/articles" class="nav-item sub-item" active-class="active">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
              <polyline points="14 2 14 8 20 8"/>
            </svg>
            <span>Articles</span>
            <span class="badge" v-if="webArticleStore.categoryCounts.all > 0">{{ webArticleStore.categoryCounts.all }}</span>
          </router-link>

          <a href="#" class="nav-item sub-item">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/>
              <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/>
            </svg>
            <span>Books</span>
          </a>

          <a href="#" class="nav-item sub-item">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
              <polyline points="22,6 12,13 2,6"/>
            </svg>
            <span>Emails</span>
          </a>

          <a href="#" class="nav-item sub-item">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z"/>
              <polyline points="14 2 14 8 20 8"/>
              <path d="M9 13h6"/>
              <path d="M9 17h3"/>
            </svg>
            <span>PDFs</span>
          </a>

          <a href="#" class="nav-item sub-item">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
              <path d="M23 3a10.9 10.9 0 0 1-3.14 1.53 4.48 4.48 0 0 0-7.86 3v1A10.66 10.66 0 0 1 3 4s-4 9 5 13a11.64 11.64 0 0 1-7 2c9 5 20 0 20-11.5a4.5 4.5 0 0 0-.08-.83A7.72 7.72 0 0 0 23 3z"/>
            </svg>
            <span>Tweets</span>
          </a>

          <a href="#" class="nav-item sub-item">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="2" y="2" width="20" height="20" rx="2.18" ry="2.18"/>
              <line x1="7" y1="2" x2="7" y2="22"/>
              <line x1="17" y1="2" x2="17" y2="22"/>
              <line x1="2" y1="12" x2="22" y2="12"/>
              <line x1="2" y1="7" x2="7" y2="7"/>
              <line x1="2" y1="17" x2="7" y2="17"/>
              <line x1="17" y1="17" x2="22" y2="17"/>
              <line x1="17" y1="7" x2="22" y2="7"/>
            </svg>
            <span>Videos</span>
          </a>

          <a href="#" class="nav-item sub-item">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 1a3 3 0 0 0-3 3v8a3 3 0 0 0 6 0V4a3 3 0 0 0-3-3z"/>
              <path d="M19 10v2a7 7 0 0 1-14 0v-2"/>
              <line x1="12" y1="19" x2="12" y2="23"/>
              <line x1="8" y1="23" x2="16" y2="23"/>
            </svg>
            <span>Podcasts</span>
          </a>

          <a href="#" class="nav-item sub-item">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
              <path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"/>
              <line x1="7" y1="7" x2="7.01" y2="7" stroke="white" stroke-width="2"/>
            </svg>
            <span>Tags</span>
          </a>
        </div>
      </div>

      <!-- Feed Section -->
      <div class="nav-section">
        <div
          class="section-header clickable"
          :class="{ expanded: expandedSections.has('feed') }"
          @click="toggleSection('feed')"
          v-show="!collapsed"
        >
          <svg
            viewBox="0 0 24 24"
            width="12"
            height="12"
            fill="currentColor"
            class="arrow-icon"
            :class="{ rotated: expandedSections.has('feed') }"
          >
            <path d="M9 18l6-6-6-6"/>
          </svg>
          <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M4 11a9 9 0 0 1 9 9"/>
            <path d="M4 4a16 16 0 0 1 16 16"/>
            <circle cx="5" cy="19" r="1"/>
          </svg>
          <span>Feed</span>
        </div>

        <div class="section-content" v-show="expandedSections.has('feed') && !collapsed">
          <!-- 按分类分组显示订阅源 -->
          <template v-for="[category, categoryFeeds] in feedStore.feedsByCategory" :key="category">
            <div
              class="category-item"
              :class="{ expanded: expandedCategories.has(category) }"
              @click="toggleCategory(category)"
            >
              <svg
                viewBox="0 0 24 24"
                width="10"
                height="10"
                fill="currentColor"
                class="arrow-icon small"
                :class="{ rotated: expandedCategories.has(category) }"
              >
                <path d="M9 18l6-6-6-6"/>
              </svg>
              <span>{{ category }}</span>
              <span class="badge" v-if="getCategoryUnreadCount(categoryFeeds) > 0">
                {{ getCategoryUnreadCount(categoryFeeds) }}
              </span>
            </div>
            <div v-show="expandedCategories.has(category)">
              <FeedItem
                v-for="feed in categoryFeeds"
                :key="feed.id"
                :feed="feed"
                :collapsed="collapsed"
                :indent="true"
              />
            </div>
          </template>

          <!-- Manage feeds -->
          <div class="nav-item sub-item manage-feeds" @click="$emit('add-feed')">
            <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="5" y1="12" x2="19" y2="12"/>
              <polyline points="12 5 19 12 12 19"/>
            </svg>
            <span>Manage feeds</span>
          </div>
        </div>
      </div>
    </nav>
  </aside>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useFeedStore } from '@/stores/feed'
import { useWebArticleStore } from '@/stores/webArticle'
import FeedItem from './FeedItem.vue'
import type { Feed } from '@/types'

defineProps<{
  collapsed: boolean
}>()

const emit = defineEmits<{
  (e: 'toggle'): void
  (e: 'add-feed'): void
  (e: 'add-url'): void
}>()

const feedStore = useFeedStore()
const webArticleStore = useWebArticleStore()

const expandedSections = ref(new Set<string>(['library', 'feed']))
const expandedCategories = ref(new Set<string>())
const showAddMenu = ref(false)
const addBtnRef = ref<HTMLButtonElement | null>(null)
const menuPosition = reactive({
  position: 'fixed' as const,
  top: '0px',
  left: '0px'
})

// 默认展开所有分类
onMounted(() => {
  feedStore.feedsByCategory.forEach((_, category) => {
    expandedCategories.value.add(category)
  })
  // Fetch web article counts
  webArticleStore.fetchCategoryCounts()
})

function getCategoryUnreadCount(feeds: Feed[]) {
  return feeds.reduce((sum, f) => sum + (f.unreadCount || 0), 0)
}

function toggleSection(section: string) {
  if (expandedSections.value.has(section)) {
    expandedSections.value.delete(section)
  } else {
    expandedSections.value.add(section)
  }
}

function toggleCategory(category: string) {
  if (expandedCategories.value.has(category)) {
    expandedCategories.value.delete(category)
  } else {
    expandedCategories.value.add(category)
  }
}

function toggleAddMenu() {
  if (!showAddMenu.value && addBtnRef.value) {
    const rect = addBtnRef.value.getBoundingClientRect()
    menuPosition.top = `${rect.top}px`
    menuPosition.left = `${rect.right + 8}px`
  }
  showAddMenu.value = !showAddMenu.value
}

// 菜单操作
function handleAddUrl() {
  showAddMenu.value = false
  emit('add-url')
}

function handleUpload() {
  showAddMenu.value = false
  // TODO: 实现上传功能
}

function handleAddEmail() {
  showAddMenu.value = false
  // TODO: 实现添加Email功能
}

function handleAddRssFeed() {
  showAddMenu.value = false
  emit('add-feed')
}

function handleAddTwitter() {
  showAddMenu.value = false
  // TODO: 实现添加Twitter功能
}

function handleAddYouTube() {
  showAddMenu.value = false
  // TODO: 实现添加YouTube功能
}

function handleConfigureIntegrations() {
  showAddMenu.value = false
  // TODO: 实现配置集成功能
}

function handleGetExtension() {
  showAddMenu.value = false
  // TODO: 实现获取浏览器扩展功能
}

function handleDownloadApps() {
  showAddMenu.value = false
  // TODO: 实现下载应用功能
}

function handleDiscoverDocuments() {
  showAddMenu.value = false
  // TODO: 实现发现新文档功能
}

function handleMoreOptions() {
  showAddMenu.value = false
  // TODO: 实现更多选项功能
}
</script>

<style lang="scss" scoped>
.sidebar {
  width: $sidebar-width;
  height: 100%;
  background-color: var(--bg-sidebar);
  border-right: 1px solid var(--border-color);
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
  overflow: hidden;

  &.collapsed {
    width: $sidebar-collapsed-width;

    .sidebar-header {
      justify-content: center;
      padding: 16px 8px;
    }
  }
}

.sidebar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 16px 12px;
}

.logo {
  display: flex;
  align-items: center;
}

.logo-text {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
  font-family: Georgia, serif;
  font-style: italic;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.icon-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border: none;
  background: none;
  color: var(--text-secondary);
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.2s;

  &:hover {
    background-color: var(--hover-color);
    color: var(--text-primary);
  }
}

.sidebar-nav {
  flex: 1;
  overflow-y: auto;
  padding: 8px 12px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 12px;
  border-radius: 6px;
  color: var(--text-secondary);
  text-decoration: none;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;

  &:hover {
    background-color: var(--hover-color);
    color: var(--text-primary);
  }

  &.active {
    background-color: var(--hover-color);
    color: var(--text-primary);
    font-weight: 500;
  }

  &.sub-item {
    padding-left: 36px;
  }

  .badge {
    margin-left: auto;
    padding: 2px 8px;
    background-color: var(--hover-color);
    color: var(--text-secondary);
    font-size: 12px;
    border-radius: 10px;
  }
}

.nav-section {
  margin-top: 8px;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  font-size: 14px;
  color: var(--text-secondary);
  border-radius: 6px;
  transition: all 0.2s;

  &.clickable {
    cursor: pointer;

    &:hover {
      background-color: var(--hover-color);
      color: var(--text-primary);
    }
  }

  &.expanded {
    color: var(--text-primary);
  }
}

.arrow-icon {
  transition: transform 0.2s;

  &.rotated {
    transform: rotate(90deg);
  }

  &.small {
    width: 10px;
    height: 10px;
  }
}

.section-content {
  margin-top: 4px;
}

.category-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px 6px 36px;
  cursor: pointer;
  color: var(--text-secondary);
  font-size: 13px;
  border-radius: 6px;
  transition: all 0.2s;

  &:hover {
    background-color: var(--hover-color);
    color: var(--text-primary);
  }

  .badge {
    margin-left: auto;
    padding: 2px 6px;
    background-color: var(--hover-color);
    color: var(--text-secondary);
    font-size: 11px;
    border-radius: 8px;
  }
}

.manage-feeds {
  color: var(--text-muted);
  font-size: 13px;

  &:hover {
    color: var(--text-secondary);
  }
}

// 添加按钮容器
.add-btn-wrapper {
  position: relative;
}
</style>

<style lang="scss">
// 全局样式 - Teleport 渲染到 body 的元素需要非 scoped 样式
.add-menu {
  position: fixed;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.12), 0 0 1px rgba(0, 0, 0, 0.1);
  min-width: 280px;
  z-index: 1001;
  overflow: hidden;
}

.add-menu-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;
}

.menu-section {
  padding: 8px 0;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 16px;
  color: #333;
  cursor: pointer;
  transition: background-color 0.15s;

  &:hover {
    background-color: #f5f5f5;
  }

  span:first-of-type {
    flex: 1;
    font-size: 14px;
  }

  .shortcut {
    font-size: 12px;
    color: #999;
  }
}

.menu-divider {
  height: 1px;
  background-color: #e8e8e8;
  margin: 0;
}
</style>

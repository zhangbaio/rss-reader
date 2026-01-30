<template>
  <div class="daily-review-page">
    <!-- 顶部导航 -->
    <header class="review-header">
      <button class="back-btn" @click="goBack">
        <svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M19 12H5M12 19l-7-7 7-7"/>
        </svg>
      </button>
      <h1 class="header-title">Daily Review</h1>
      <span class="progress-indicator" v-if="!isCompleted && reviewList.length > 0">
        {{ progress.current }} of {{ progress.total }}
      </span>
    </header>

    <!-- 主内容区域 -->
    <main class="review-content">
      <!-- 加载状态 -->
      <div class="loading-state" v-if="loading">
        <div class="spinner"></div>
        <p>Loading your highlights...</p>
      </div>

      <!-- 空状态 -->
      <div class="empty-state" v-else-if="reviewList.length === 0">
        <div class="empty-icon">
          <svg viewBox="0 0 24 24" width="64" height="64" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"/>
          </svg>
        </div>
        <h2>No highlights to review</h2>
        <p>Start reading and highlighting articles to build your review list.</p>
        <router-link to="/rss" class="btn-primary">Go to Reader</router-link>
      </div>

      <!-- 完成状态 -->
      <div class="complete-state" v-else-if="isCompleted">
        <div class="complete-icon">
          <svg viewBox="0 0 24 24" width="64" height="64" fill="none" stroke="#4CAF50" stroke-width="2">
            <path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
          </svg>
        </div>
        <h2>Review Complete!</h2>
        <p>You've reviewed {{ stats.todayReviewed }} highlights today.</p>
        <div class="complete-actions">
          <button class="btn-outline" @click="resetReview">Review Again</button>
          <router-link to="/" class="btn-primary">Back to Home</router-link>
        </div>
      </div>

      <!-- 高亮卡片 -->
      <div class="highlight-card" v-else-if="currentHighlight">
        <!-- 来源信息 -->
        <div class="source-info">
          <div class="source-left">
            <img
              v-if="currentHighlight.article?.feedImageUrl"
              :src="currentHighlight.article.feedImageUrl"
              class="source-icon"
              @error="handleIconError"
            />
            <div class="source-icon-placeholder" v-else>
              <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                <path d="M4 11a9 9 0 0 1 9 9M4 4a16 16 0 0 1 16 16M5 19a1 1 0 1 1-2 0 1 1 0 0 1 2 0z"/>
              </svg>
            </div>
            <div class="source-text">
              <span class="source-name">{{ currentHighlight.article?.feedName || 'Unknown Source' }}</span>
              <a :href="currentHighlight.article?.link" target="_blank" class="source-link">
                {{ getDomain(currentHighlight.article?.link) }}
              </a>
            </div>
          </div>
          <div class="source-actions">
            <button class="icon-btn" title="Bookmark">
              <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/>
              </svg>
            </button>
            <button class="icon-btn" title="More">
              <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M9 5l7 7-7 7"/>
              </svg>
            </button>
          </div>
        </div>

        <!-- 高亮内容 -->
        <div class="highlight-content" :style="{ borderLeftColor: currentHighlight.color || '#5c7cfa' }">
          <p>{{ currentHighlight.selectedText }}</p>
        </div>

        <!-- 标签 -->
        <div class="highlight-tags" v-if="currentHighlight.tags && currentHighlight.tags.length > 0">
          <span
            v-for="tag in currentHighlight.tags"
            :key="tag.id"
            class="tag"
            :style="{ backgroundColor: tag.color || '#5c7cfa' }"
          >
            {{ tag.name }}
            <button class="tag-remove">×</button>
          </span>
        </div>

        <!-- 快捷操作 -->
        <div class="quick-actions">
          <button class="quick-btn" title="Reply">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"/>
            </svg>
          </button>
          <button class="quick-btn" title="Favorite">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
            </svg>
          </button>
          <button class="quick-btn" title="Share">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="18" cy="5" r="3"/>
              <circle cx="6" cy="12" r="3"/>
              <circle cx="18" cy="19" r="3"/>
              <line x1="8.59" y1="13.51" x2="15.42" y2="17.49"/>
              <line x1="15.41" y1="6.51" x2="8.59" y2="10.49"/>
            </svg>
          </button>
          <button class="quick-btn" title="Copy">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="9" y="9" width="13" height="13" rx="2" ry="2"/>
              <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"/>
            </svg>
          </button>
        </div>

        <!-- 笔记区域 -->
        <div class="note-section" v-if="currentHighlight.note">
          <div class="note-label">NOTE</div>
          <p class="note-content">{{ currentHighlight.note }}</p>
        </div>
      </div>
    </main>

    <!-- 底部操作栏 -->
    <footer class="review-actions" v-if="!loading && !isCompleted && reviewList.length > 0">
      <button class="action-btn discard" @click="handleAction('discard')">
        <svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="2">
          <line x1="18" y1="6" x2="6" y2="18"/>
          <line x1="6" y1="6" x2="18" y2="18"/>
        </svg>
        <span>Discard</span>
      </button>
      <button class="action-btn master" @click="handleAction('master')">
        <svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="2">
          <rect x="2" y="3" width="20" height="14" rx="2" ry="2"/>
          <line x1="8" y1="21" x2="16" y2="21"/>
          <line x1="12" y1="17" x2="12" y2="21"/>
        </svg>
        <span>Master</span>
      </button>
      <button class="action-btn feedback" @click="handleAction('feedback')">
        <svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="12" cy="12" r="10"/>
          <polyline points="12 6 12 12 16 14"/>
        </svg>
        <span>Feedback</span>
      </button>
      <button class="action-btn keep active" @click="handleAction('keep')">
        <svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="2">
          <polyline points="20 6 9 17 4 12"/>
        </svg>
        <span>Keep</span>
      </button>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { useReviewStore } from '@/stores/review'
import type { ReviewAction } from '@/types'

const router = useRouter()
const reviewStore = useReviewStore()

const {
  reviewList,
  currentHighlight,
  progress,
  loading,
  isCompleted,
  stats
} = storeToRefs(reviewStore)

onMounted(async () => {
  await reviewStore.fetchDailyReview()
  await reviewStore.fetchStats()
})

function goBack() {
  router.push('/')
}

function handleAction(action: ReviewAction) {
  reviewStore.recordAction(action)
}

function resetReview() {
  reviewStore.reset()
}

function getDomain(url?: string) {
  if (!url) return ''
  try {
    return new URL(url).hostname
  } catch {
    return url
  }
}

function handleIconError(e: Event) {
  const img = e.target as HTMLImageElement
  img.style.display = 'none'
}
</script>

<style lang="scss" scoped>
.daily-review-page {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f5f7fa;
}

// 顶部导航
.review-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  background-color: #fff;
  border-bottom: 1px solid #e8e8e8;
}

.back-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border: none;
  background: none;
  color: #666;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.2s;

  &:hover {
    background-color: #f0f0f0;
    color: #333;
  }
}

.header-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin: 0;
}

.progress-indicator {
  font-size: 14px;
  color: #666;
}

// 主内容区域
.review-content {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 32px 20px;
  overflow-y: auto;
}

// 加载状态
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 48px;

  .spinner {
    width: 40px;
    height: 40px;
    border: 3px solid #e8e8e8;
    border-top-color: #5c7cfa;
    border-radius: 50%;
    animation: spin 1s linear infinite;
  }

  p {
    margin-top: 16px;
    color: #666;
  }
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

// 空状态
.empty-state,
.complete-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 48px 24px;

  h2 {
    margin: 24px 0 8px;
    font-size: 20px;
    color: #333;
  }

  p {
    margin: 0 0 24px;
    color: #666;
    font-size: 14px;
  }
}

.empty-icon,
.complete-icon {
  color: #999;
}

.complete-actions {
  display: flex;
  gap: 12px;
}

.btn-primary {
  padding: 12px 24px;
  background-color: #5c7cfa;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  text-decoration: none;
  cursor: pointer;
  transition: background-color 0.2s;

  &:hover {
    background-color: #4c6ef5;
  }
}

.btn-outline {
  padding: 12px 24px;
  background-color: #fff;
  color: #333;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    border-color: #5c7cfa;
    color: #5c7cfa;
  }
}

// 高亮卡片
.highlight-card {
  width: 100%;
  max-width: 600px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

// 来源信息
.source-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.source-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.source-icon {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  object-fit: contain;
}

.source-icon-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background-color: #5c7cfa;
  color: #fff;
  border-radius: 6px;
}

.source-text {
  display: flex;
  flex-direction: column;
}

.source-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.source-link {
  font-size: 12px;
  color: #999;
  text-decoration: none;

  &:hover {
    color: #5c7cfa;
  }
}

.source-actions {
  display: flex;
  gap: 8px;
}

.icon-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border: none;
  background: none;
  color: #999;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.2s;

  &:hover {
    background-color: #f5f5f5;
    color: #333;
  }
}

// 高亮内容
.highlight-content {
  padding: 24px 20px;
  border-left: 4px solid #5c7cfa;
  margin: 0 20px;
  background-color: #fafafa;

  p {
    margin: 0;
    font-size: 15px;
    line-height: 1.7;
    color: #333;
  }
}

// 标签
.highlight-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 16px 20px;
}

.tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  background-color: #5c7cfa;
  color: #fff;
  font-size: 12px;
  border-radius: 4px;
}

.tag-remove {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 16px;
  height: 16px;
  border: none;
  background: rgba(255, 255, 255, 0.3);
  color: #fff;
  font-size: 14px;
  border-radius: 50%;
  cursor: pointer;
  margin-left: 4px;

  &:hover {
    background: rgba(255, 255, 255, 0.5);
  }
}

// 快捷操作
.quick-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding: 12px 20px;
  border-top: 1px solid #f0f0f0;
}

.quick-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border: none;
  background: none;
  color: #999;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.2s;

  &:hover {
    background-color: #f5f5f5;
    color: #5c7cfa;
  }
}

// 笔记区域
.note-section {
  padding: 16px 20px;
  border-top: 1px solid #f0f0f0;
  background-color: #fafafa;
}

.note-label {
  font-size: 11px;
  font-weight: 600;
  color: #999;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 8px;
}

.note-content {
  margin: 0;
  font-size: 14px;
  line-height: 1.6;
  color: #555;
}

// 底部操作栏
.review-actions {
  display: flex;
  justify-content: center;
  gap: 32px;
  padding: 20px;
  background-color: #fff;
  border-top: 1px solid #e8e8e8;
}

.action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  border: none;
  background: none;
  color: #666;
  cursor: pointer;
  border-radius: 12px;
  transition: all 0.2s;

  span {
    font-size: 12px;
  }

  &:hover {
    background-color: #f5f5f5;
  }

  &.active {
    color: #fff;
    background-color: #5c7cfa;

    &:hover {
      background-color: #4c6ef5;
    }
  }

  &.discard:hover {
    color: #e74c3c;
  }

  &.master:hover {
    color: #9b59b6;
  }

  &.feedback:hover {
    color: #f39c12;
  }
}

// 响应式
@media (max-width: 640px) {
  .review-actions {
    gap: 16px;
    padding: 16px;
  }

  .action-btn {
    padding: 10px 12px;
  }

  .highlight-content {
    margin: 0 16px;
    padding: 20px 16px;
  }
}
</style>
